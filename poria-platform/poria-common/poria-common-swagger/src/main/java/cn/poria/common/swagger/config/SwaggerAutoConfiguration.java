package cn.poria.common.swagger.config;

import cn.poria.common.swagger.support.SwaggerProperties;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * swagger配置
 *
 * <p>
 * 禁用方法1：使用注解@Profile({"dev","test"})
 *
 * 表示在开发或测试环境开启，而在生产关闭。（推荐使用） 禁用方法2：使用注解@ConditionalOnProperty(name = "swagger.enable",
 *
 * havingValue = "true") 然后在测试配置或者开发配置中添加swagger.enable=true即可开启，生产环境不填则默认关闭Swagger.
 * </p>
 *
 * @author shanxincd
 */
@RequiredArgsConstructor
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
public class SwaggerAutoConfiguration {

	private final SwaggerProperties swaggerProperties;

//	@Bean
//	public OpenAPI springOpenAPI() {
//		OpenAPI openAPI = new OpenAPI().info(new Info().title(swaggerProperties.getTitle()));
//		// oauth2.0 password
//		openAPI.schemaRequirement(HttpHeaders.AUTHORIZATION, this.securityScheme());
//		// servers
//		List<Server> serverList = new ArrayList<>();
////		String path = swaggerProperties.getServices().get(serviceInstance.getServiceId());
////		serverList.add(new Server().url(swaggerProperties.getGateway() + "/" + path));
//		openAPI.servers(serverList);
//		return openAPI;
//	}

	private SecurityScheme securityScheme() {
		OAuthFlow clientCredential = new OAuthFlow();
		clientCredential.setTokenUrl(swaggerProperties.getTokenUrl());
		clientCredential.setScopes(new Scopes().addString(swaggerProperties.getScope(), swaggerProperties.getScope()));
		OAuthFlows oauthFlows = new OAuthFlows();
		oauthFlows.password(clientCredential);
		SecurityScheme securityScheme = new SecurityScheme();
		securityScheme.setType(SecurityScheme.Type.OAUTH2);
		securityScheme.setFlows(oauthFlows);
		return securityScheme;
	}

}
