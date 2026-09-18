
package cn.poria.common.swagger.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SwaggerProperties
 *
 * @author shanxincd
 * @date 2018/7/25 14:00
 */
@Data
@ConfigurationProperties("swagger")
public class SwaggerProperties {

	/**
	 * 是否开启swagger
	 */
	private Boolean enabled = true;

	/**
	 * swagger会解析的包路径
	 **/
	private String basePackage = "";

	/**
	 * swagger会解析的url规则
	 **/
	private List<String> basePath = new ArrayList<>();

	/**
	 * 在basePath基础上需要排除的url规则
	 **/
	private List<String> excludePath = new ArrayList<>();

	/**
	 * 需要排除的服务
	 */
	private List<String> ignoreProviders = new ArrayList<>();

	/**
	 * 标题
	 **/
	private String title = "";

	/**
	 * 网关
	 */
	private String gateway;

	/**
	 * 获取token
	 */
	private String tokenUrl;

	/**
	 * 作用域
	 */
	private String scope;

	/**
	 * 服务转发配置
	 */
	private Map<String, String> services;

}
