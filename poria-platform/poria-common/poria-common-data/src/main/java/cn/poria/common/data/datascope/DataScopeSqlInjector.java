package cn.poria.common.data.datascope;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 支持自定义数据权限方法注入
 *
 * @author shanxincd
 * @date 2020-06-17
 */
public class DataScopeSqlInjector extends DefaultSqlInjector {

	@Override
	public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
		List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
		methodList.add(new SelectListByScope());
		methodList.add(new SelectPageByScope());
		methodList.add(new SelectCountByScope());
		return methodList;
	}

}
