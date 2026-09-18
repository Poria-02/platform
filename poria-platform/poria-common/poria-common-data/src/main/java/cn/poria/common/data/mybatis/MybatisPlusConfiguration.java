package cn.poria.common.data.mybatis;

import cn.poria.common.data.datascope.DataScopeInnerInterceptor;
import cn.poria.common.data.datascope.DataScopeInterceptor;
import cn.poria.common.data.datascope.DataScopeSqlInjector;
import cn.poria.common.data.datascope.PlatDefaultDatascopeHandle;
import cn.poria.common.data.handler.EncryptTypeHandler;
import cn.poria.common.data.handler.PlatMetaObjectHandler;
import cn.poria.common.data.resolver.SqlFilterArgumentResolver;
import cn.poria.common.security.service.PlatUser;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;

/**
 * @author shanxincd
 * @date 2020-02-08
 */
@Configuration
@Slf4j
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@MapperScan({"cn.poria.**.dao","cn.poria.**.mapper"})
public class MybatisPlusConfiguration implements WebMvcConfigurer {

	/**
	 * 增加请求参数解析器，对请求中的参数注入SQL 检查
	 * @param resolverList
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolverList) {
		resolverList.add(new SqlFilterArgumentResolver());
	}

	/**
	 * mybatis plus 拦截器配置
	 * @return PigxDefaultDatascopeHandle
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor(DataScopeInterceptor dataScopeInterceptor) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 数据权限
		interceptor.addInnerInterceptor(dataScopeInterceptor);
		// 分页支持
		PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
		paginationInnerInterceptor.setMaxLimit(1000L);
		interceptor.addInnerInterceptor(paginationInnerInterceptor);
		return interceptor;
	}



	/**
	 * 数据权限拦截器
	 * @return DataScopeInterceptor
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(PlatUser.class)
	public DataScopeInterceptor dataScopeInterceptor(RestTemplate restTemplate) {
		DataScopeInnerInterceptor dataScopeInnerInterceptor = new DataScopeInnerInterceptor();
		dataScopeInnerInterceptor.setDataScopeHandle(new PlatDefaultDatascopeHandle(restTemplate));
		return dataScopeInnerInterceptor;
	}

	/**
	 * 扩展 mybatis-plus baseMapper 支持数据权限
	 * @return
	 */
	@Bean
	@ConditionalOnBean(DataScopeInterceptor.class)
	public DataScopeSqlInjector dataScopeSqlInjector() {
		return new DataScopeSqlInjector();
	}

	@Bean
	public PlatMetaObjectHandler  platMetaObjectHandler(){
		return new PlatMetaObjectHandler();
	}


	/**
	 * 数据库方言配置
	 * @return
	 */
	@Bean
	public DatabaseIdProvider databaseIdProvider() {
		VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
		Properties properties = new Properties();
		properties.setProperty("SQL Server", "mssql");
		databaseIdProvider.setProperties(properties);
		return databaseIdProvider;
	}

	/**
	 * 敏感数据加密(例身份证)
	 * 实体类添加@TableName(value = "表名",autoResultMap = true)
	 * 加密字段上添加 @TableField(typeHandler = EncryptTypeHandler.class)
	 * 使用sql查询时需要对加密字段映射配置 例如：添加 gender 字段的映射配置 -->
	 *  <result column="gender" property="gender" jdbcType="INTEGER" typeHandler="cn.poria.common.data.handler.EncryptTypeHandler"/>
	 * @return
	 * @throws IOException
	 */
	@Bean
	public  EncryptTypeHandler encryptTypeHandler(){
		return new EncryptTypeHandler();
	}

	@SneakyThrows
	@Bean
	public IdentifierGenerator idGenerator() {
		// 根据机器IP或其他唯一标识设置workerId
		return new DefaultIdentifierGenerator(InetAddress.getLocalHost());
	}
}
