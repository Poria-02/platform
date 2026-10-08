package cn.poria.common.data.datascope;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import lombok.Setter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shanxincd
 * @date 2020/11/29
 */
public class DataScopeInnerInterceptor implements DataScopeInterceptor {

	@Setter
	private DataScopeHandle dataScopeHandle;

	@Override
	public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
			ResultHandler resultHandler, BoundSql boundSql) {
		PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);

		String originalSql = boundSql.getSql();
		Object parameterObject = boundSql.getParameterObject();

		// 查找参数中包含DataScope类型的参数
		DataScope dataScope = findDataScopeObject(parameterObject);
		if (dataScope == null) {
			return;
		}

		// 返回true 不拦截直接返回原始 SQL
		boolean unrestricted = dataScopeHandle.calcScope(dataScope);
		boolean count = dataScope.getFunc() == DataScopeFuncEnum.COUNT
				|| ms.getId().endsWith(".selectCountByScope");
		if (unrestricted && !count) {
			return;
		}
		OrderedSql ordered = moveOrderBy(originalSql);

		List<Long> deptIds = dataScope.getResolvedDeptList();
		String condition = "1 = 2";
		if (dataScope.isAllDepartments() || (deptIds != null && !deptIds.isEmpty())) {
			condition = dataScope.isAllDepartments() ? "1 = 1"
					: "temp_data_scope." + column(dataScope.getScopeDeptName())
					+ " IN (" + CollectionUtil.join(deptIds, ",") + ")";
			if (StrUtil.isNotBlank(dataScope.getUsername())) {
				condition += " AND temp_data_scope." + column(dataScope.getScopeUserName()) + " = ?";
				List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
				String key = "__data_scope_username";
				mappings.add(mappings.size() - ordered.orderParameters(),
						new ParameterMapping.Builder(ms.getConfiguration(), key, String.class).build());
				mpBs.parameterMappings(mappings);
				boundSql.setAdditionalParameter(key, dataScope.getUsername());
			}
		}
		originalSql = String.format("SELECT %s FROM (%s) temp_data_scope WHERE %s%s",
				count ? "COUNT(1)" : "*", ordered.sql(), condition, count ? "" : ordered.orderBy());
		if (count && ordered.orderParameters() > 0) {
			List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
			mappings.subList(mappings.size() - ordered.orderParameters(), mappings.size()).clear();
			mpBs.parameterMappings(mappings);
		}

		mpBs.sql(originalSql);
	}

	private String column(String name) {
		if (name == null || !name.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
			throw new IllegalArgumentException("数据权限字段名称不合法");
		}
		return name;
	}

	/** 排序必须在外层生效，并将原表别名映射到派生表输出列。 */
	private OrderedSql moveOrderBy(String sql) {
		try {
			Select select = (Select) CCJSqlParserUtil.parse(sql);
			var order = select.getOrderByElements();
			if (order == null || order.isEmpty()) {
				return new OrderedSql(select.toString(), "", 0);
			}
			// 数据范围必须先于分页计算；已有分页应由后续分页拦截器处理。
			if (select.getLimit() != null || select.getOffset() != null || select.getFetch() != null) {
				throw new IllegalArgumentException("数据权限查询请通过 MyBatis 分页参数设置分页，不能预先限制行数");
			}
			Map<String, String> names = new java.util.HashMap<>();
			if (select instanceof PlainSelect plain) {
				for (SelectItem<?> item : plain.getSelectItems()) {
					String alias = item.getAlias() == null ? null : item.getAlias().getName();
					if (alias != null) {
						names.put(item.getExpression().toString(), alias);
					}
					if (item.getExpression() instanceof Column source) {
						String output = alias == null ? source.getColumnName() : alias;
						names.put(source.toString(), output);
						names.putIfAbsent(source.getColumnName(), output);
					}
				}
			}
			int[] parameters = {0};
			ExpressionVisitorAdapter<Void> visitor = new ExpressionVisitorAdapter<>() {
				@Override
				public <S> Void visit(Column field, S context) {
					String name = names.getOrDefault(field.toString(),
							names.getOrDefault(field.getColumnName(), field.getColumnName()));
					field.setTable(new Table("temp_data_scope"));
					field.setColumnName(name);
					return null;
				}

				@Override
				public <S> Void visit(JdbcParameter parameter, S context) {
					parameters[0]++;
					return null;
				}
			};
			for (var element : order) {
				String alias = names.get(element.getExpression().toString());
				if (alias != null) {
					element.setExpression(new Column(new Table("temp_data_scope"), alias));
				} else {
					element.getExpression().accept(visitor, null);
				}
			}
			select.setOrderByElements(null);
			return new OrderedSql(select.toString(), Select.orderByToString(order), parameters[0]);
		} catch (JSQLParserException ex) {
			throw new IllegalArgumentException("无法解析数据权限查询 SQL", ex);
		}
	}

	private record OrderedSql(String sql, String orderBy, int orderParameters) { }

	/**
	 * 查找参数是否包括DataScope对象
	 * @param parameterObj 参数列表
	 * @return DataScope
	 */
	private DataScope findDataScopeObject(Object parameterObj) {
		if (parameterObj instanceof DataScope) {
			return (DataScope) parameterObj;
		}
		else if (parameterObj instanceof Map) {
			for (Object val : ((Map<?, ?>) parameterObj).values()) {
				if (val instanceof DataScope) {
					return (DataScope) val;
				}
			}
		}
		return null;
	}

}
