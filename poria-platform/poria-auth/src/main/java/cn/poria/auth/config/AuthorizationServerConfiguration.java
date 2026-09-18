/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.poria.auth.config;

import cn.poria.auth.core.CustomeOAuth2TokenCustomizer;
import cn.poria.auth.service.PlatRemoteRegisteredClientRepository;
import cn.poria.auth.support.plat.*;
import cn.poria.auth.support.CustomeOAuth2AccessTokenGenerator;
import cn.poria.common.core.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.Arrays;

/**
 * @author lengleng
 * @date 2022/5/27
 *
 * 认证服务器配置
 */
@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfiguration {

	private final OAuth2AuthorizationService authorizationService;

	private final PlatUserServiceContext platUserServiceContext;

	private final AuthConfigProperties authConfigProperties;

	/**
	 * 用于 协议端点 的 Spring Security 过滤器链
	 * @param http
	 * @return SecurityFilterChain
	 * @throws Exception
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
																	  PlatAuthenticationSuccessHandler platAuthenticationSuccessHandler,
																	  PlatAuthenticationFailureHandler platAuthenticationFailureHandler) throws Exception {

		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				//个性化Token Endpoint
				.tokenEndpoint(tokenEndpoint -> tokenEndpoint
						.accessTokenRequestConverter(accessTokenRequestConverter()) // 注入自定义的授权认证Converter
						.accessTokenResponseHandler(platAuthenticationSuccessHandler) // 登录成功处理器
						.errorResponseHandler(platAuthenticationFailureHandler))// 登录失败处理器

				// 个性化客户端认证
				.clientAuthentication(clientAuthentication -> clientAuthentication
						.errorResponseHandler(platAuthenticationFailureHandler))// 处理客户端认证异常
				//个性化Authorization Endpoint.
				.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint// 授权码端点个性化confirm页面
						.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI)
						.errorResponseHandler(platAuthenticationFailureHandler))

				.authorizationService(authorizationService)
				// 个性化授权服务配置
				.authorizationServerSettings(authorizationServerSettings());

		// 注入自定义授权模式实现
		addCustomOAuth2GrantAuthenticationProvider(http);

		return http.build();
	}

	/**
	 * 用于 认证 的 Spring Security 过滤器链。
	 * @param http
	 * @return SecurityFilterChain
	 * @throws Exception
	 */
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
				return http.authorizeHttpRequests(authorize -> authorize
							// 自定义接口、端点暴露
							.requestMatchers("/token/**", "/actuator/**", "/css/**", "/error","/plat/login").permitAll()
							.anyRequest().authenticated())
						.csrf(AbstractHttpConfigurer::disable)
						.build();
	}

	/**
	 * 令牌生成规则实现 </br>
	 * client:username:uuid
	 * @return OAuth2TokenGenerator
	 */
	@Bean
	public OAuth2TokenGenerator oAuth2TokenGenerator() {
		CustomeOAuth2AccessTokenGenerator accessTokenGenerator = new CustomeOAuth2AccessTokenGenerator();
		// 注入Token 增加关联用户信息
		accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());
		return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
	}

	/**
	 * request -> xToken 注入请求转换器
	 * @return DelegatingAuthenticationConverter
	 */
	private AuthenticationConverter accessTokenRequestConverter() {
		return new DelegatingAuthenticationConverter(Arrays.asList(
				new PlatAuthenticationConverter(authConfigProperties),
				new OAuth2RefreshTokenAuthenticationConverter(),
				new OAuth2ClientCredentialsAuthenticationConverter(),
				new OAuth2AuthorizationCodeAuthenticationConverter()));
	}

	/**
	 * 注入授权模式实现提供方
	 */
	@SuppressWarnings("unchecked")
	private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http) {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);

		PlatAuthenticationProvider platAuthenticationProvider = new PlatAuthenticationProvider(authenticationManager,authorizationService,oAuth2TokenGenerator());
		platAuthenticationProvider.setUserDetailsService(platUserServiceContext);
		// 处理 PlatAuthenticationToken
		http.authenticationProvider(platAuthenticationProvider);
	}



	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder()
				.issuer(SecurityConstants.PROJECT_LICENSE)
				//token端点 提供客户端获取token的能力
				.tokenEndpoint("/plat/login")
				//授权端点 接收授权请求，处理用户授权，该请求需要经过认证的用户才能正常访问。
				.authorizationEndpoint("/plat/authorize")
				.build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		return new PlatRemoteRegisteredClientRepository();
	}
}
