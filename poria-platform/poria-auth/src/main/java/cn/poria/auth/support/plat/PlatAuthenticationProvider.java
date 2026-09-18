package cn.poria.auth.support.plat;

import cn.hutool.extra.spring.SpringUtil;
import cn.poria.auth.utils.OAuth2ErrorCodesExpand;
import cn.poria.auth.utils.ScopeException;
import cn.poria.common.security.service.PlatAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
public class PlatAuthenticationProvider implements AuthenticationProvider {

	private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    private PlatUserServiceContext platUserServiceContext;

	private final AuthenticationManager authenticationManager;

	private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

	private final OAuth2AuthorizationService authorizationService;

	@Deprecated
	private Supplier<String> refreshTokenGenerator;


	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

//	private final MessageSourceAccessor messages;

	/**
	 * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
	 * provided parameters.
	 * @param authorizationService the authorization service
	 * @param tokenGenerator the token generator
	 * @since 0.2.3
	 */
	public PlatAuthenticationProvider(AuthenticationManager authenticationManager,
														 OAuth2AuthorizationService authorizationService,
														 OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
		Assert.notNull(authorizationService, "authorizationService cannot be null");
		Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
		this.authenticationManager = authenticationManager;
		this.authorizationService = authorizationService;
		this.tokenGenerator = tokenGenerator;
		// 国际化配置
//		this.messages = new MessageSourceAccessor(SpringUtil.getBean("securityMessageSource"), Locale.CHINA);
	}

    /**
     * Performs authentication with the same contract as
     * {@link AuthenticationManager#authenticate(Authentication)}
     * .
     *
     * @param authentication the authentication request object.
     * @return a fully authenticated object including credentials. May return
     * <code>null</code> if the <code>AuthenticationProvider</code> is unable to support
     * authentication of the passed <code>Authentication</code> object. In such a case,
     * the next <code>AuthenticationProvider</code> that supports the presented
     * <code>Authentication</code> class will be tried.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		PlatAuthenticationToken platAuthenticationToken = (PlatAuthenticationToken) authentication;
		OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(platAuthenticationToken);

		//client
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

		Set<String> authorizedScopes;

		// Default to configured scopes
		if (!CollectionUtils.isEmpty(platAuthenticationToken.getScopes())) {
			for (String requestedScope : platAuthenticationToken.getScopes()) {
				if (!registeredClient.getScopes().contains(requestedScope)) {
					throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
				}
			}
			authorizedScopes = new LinkedHashSet<>(platAuthenticationToken.getScopes());
		}
		else {
			throw new ScopeException(OAuth2ErrorCodesExpand.SCOPE_IS_EMPTY);
		}

		try {

			UserDetails userDetails = platUserServiceContext.loadUser(platAuthenticationToken);

			if(userDetails == null){
				throw new InternalAuthenticationServiceException("登录信息错误");
			}

			if(!ENCODER.matches(platAuthenticationToken.getLoginModel().getPassword(),userDetails.getPassword())){
				throw new BadCredentialsException("登录信息错误");
			}

			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
					new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());

			// @formatter:off
			DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
					.registeredClient(registeredClient)
					.principal(usernamePasswordAuthenticationToken)
					.authorizationServerContext(AuthorizationServerContextHolder.getContext())
					.authorizedScopes(authorizedScopes)
					.authorizationGrantType(AuthorizationGrantType.PASSWORD)
					.authorizationGrant(platAuthenticationToken);
			// @formatter:on

			OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
					.withRegisteredClient(registeredClient).principalName(userDetails.getUsername())
					.authorizationGrantType(AuthorizationGrantType.PASSWORD)
					.authorizedScopes(authorizedScopes);

			// ----- Access token -----
			OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
			OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
			if (generatedAccessToken == null) {
				OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
						"The token generator failed to generate the access token.", ERROR_URI);
				throw new OAuth2AuthenticationException(error);
			}
			OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
					generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
					generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
			if (generatedAccessToken instanceof ClaimAccessor) {
				authorizationBuilder.id(accessToken.getTokenValue())
						.token(accessToken,
								(metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
										((ClaimAccessor) generatedAccessToken).getClaims()))
						// 0.4.0 新增的方法
						.authorizedScopes(authorizedScopes)
						.attribute(Principal.class.getName(), usernamePasswordAuthenticationToken);
			}
			else {
				authorizationBuilder.id(accessToken.getTokenValue()).accessToken(accessToken);
			}

			// ----- Refresh token -----
			OAuth2RefreshToken refreshToken = null;
			if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
					// Do not issue refresh token to public client
					!clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

				if (this.refreshTokenGenerator != null) {
					Instant issuedAt = Instant.now();
					Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getRefreshTokenTimeToLive());
					refreshToken = new OAuth2RefreshToken(this.refreshTokenGenerator.get(), issuedAt, expiresAt);
				}
				else {
					tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
					OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
					if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
						OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
								"The token generator failed to generate the refresh token.", ERROR_URI);
						throw new OAuth2AuthenticationException(error);
					}
					refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
				}
				authorizationBuilder.refreshToken(refreshToken);
			}

			OAuth2Authorization authorization = authorizationBuilder.build();

			this.authorizationService.save(authorization);

			log.debug("returning OAuth2AccessTokenAuthenticationToken");

			return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken,
					refreshToken, Objects.requireNonNull(authorization.getAccessToken().getClaims()));

		}
		catch (Exception ex) {
			log.error("problem in authenticate", ex);
			throw oAuth2AuthenticationException(authentication, (AuthenticationException) ex);
		}

    }


    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
     * indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the presented
     * instance of the <code>Authentication</code> class. It simply indicates it can
     * support closer evaluation of it. An <code>AuthenticationProvider</code> can still
     * return <code>null</code> from the {@link #authenticate(Authentication)} method to
     * indicate another <code>AuthenticationProvider</code> should be tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     *
     * @param authentication
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
	@Override
	public boolean supports(Class<?> authentication){
		return PlatAuthenticationToken.class.isAssignableFrom(authentication);
	}


    public void setUserDetailsService(PlatUserServiceContext platUserServiceContext) {
        this.platUserServiceContext = platUserServiceContext;
    }


	private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(
			Authentication authentication) {

		OAuth2ClientAuthenticationToken clientPrincipal = null;

		if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
		}

		if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
			return clientPrincipal;
		}

		throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
	}

	/**
	 * 登录异常转换为oauth2异常
	 * @param authentication 身份验证
	 * @param authenticationException 身份验证异常
	 * @return {@link OAuth2AuthenticationException}
	 */
	private OAuth2AuthenticationException oAuth2AuthenticationException(Authentication authentication,
																		AuthenticationException authenticationException) {
		if (authenticationException instanceof UsernameNotFoundException) {
			return new OAuth2AuthenticationException(
					new OAuth2Error(OAuth2ErrorCodesExpand.USERNAME_NOT_FOUND,authenticationException.getMessage(), ""));
		}
		if (authenticationException instanceof BadCredentialsException) {
			return new OAuth2AuthenticationException(
					new OAuth2Error(OAuth2ErrorCodesExpand.BAD_CREDENTIALS,authenticationException.getMessage(), ""));
		}
		if (authenticationException instanceof LockedException) {
			return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USER_LOCKED, "账号已锁定", ""));
		}
		if (authenticationException instanceof DisabledException) {
			return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USER_DISABLE,authenticationException.getMessage(),""));
		}
		if (authenticationException instanceof AccountExpiredException) {
			return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USER_EXPIRED, "账号已过期", ""));
		}
		if (authenticationException instanceof CredentialsExpiredException) {
			return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.CREDENTIALS_EXPIRED,"密码已过期",""));
		}
		if (authenticationException instanceof ScopeException) {
			return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE,"无效的授权范围", ""));
		}
		return new OAuth2AuthenticationException(OAuth2ErrorCodesExpand.UN_KNOW_LOGIN_ERROR);
	}

}
