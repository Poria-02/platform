package cn.poria.common.data.datascope;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author shanxincd
 * @date 2018/8/30 数据权限查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataScope extends HashMap {

	/**
	 * 限制范围的字段名称
	 */
	private String scopeDeptName = "dept_id";

	/**
	 * 本人权限范围字段
	 */
	private String scopeUserName = "create_by";

	/**
	 * 具体的数据范围
	 */
	private List<Long> deptList = new ArrayList<>();

	/** 处理器计算出的有效范围，与调用方的查询条件分开保存。 */
	private List<Long> resolvedDeptList = new ArrayList<>();

	/** 是否具有全部部门权限，由处理器重新计算。 */
	private boolean allDepartments;

	/**
	 * 具体查询的用户数据权限范围
	 */
	private String username;

	/**
	 * 是否只查询本部门
	 */
	private Boolean isOnly = false;

	/**
	 * 函数名称，默认 SELECT * ;
	 *
	 * <ul>
	 * <li>COUNT(1)</li>
	 * </ul>
	 */
	private DataScopeFuncEnum func = DataScopeFuncEnum.ALL;

	/**
	 * of 获取实例
	 */
	public static DataScope of() {
		return new DataScope();
	}

	public DataScope deptIds(List<Long> deptIds) {
		this.deptList = deptIds;
		return this;
	}

	public DataScope only(boolean isOnly) {
		this.isOnly = isOnly;
		return this;
	}

	public DataScope func(DataScopeFuncEnum func) {
		this.func = func;
		return this;
	}

}
