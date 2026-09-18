package cn.poria.auth.support.plat;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import cn.poria.auth.config.AuthConfigProperties;
import cn.poria.auth.utils.OAuth2EndpointUtils;
import cn.poria.auth.utils.ServletUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.security.service.PlatAuthenticationToken;
import cn.poria.common.security.service.PlatLoginModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jumuning
 * @date 2022-06-02
 * <p>
 * 自定义模式认证转换器
 */
@Slf4j
@AllArgsConstructor
public class PlatAuthenticationConverter implements AuthenticationConverter {

    private AuthConfigProperties authConfigProperties;

    @Override
    public Authentication convert(HttpServletRequest request) {

        if (!StrUtil.equals(SecurityConstants.PLAT, request.getParameter(OAuth2ParameterNames.GRANT_TYPE))) {
            return null;
        }

        PlatLoginModel loginModel = JSONUtil.toBean(ServletUtil.getBody(request), PlatLoginModel.class);
        log.info("请求参数:{}", JSONUtil.toJsonStr(loginModel));

        //密码解密 如果是123456内部不验证
        if (authConfigProperties.enable && StrUtil.isNotBlank(loginModel.getPassword()) && !StrUtil.equals(loginModel.getPassword(), "123456")) {
            AES aes = new AES(Mode.CFB, Padding.NoPadding,
                    new SecretKeySpec(authConfigProperties.getEncodeKey().getBytes(), "AES"),
                    new IvParameterSpec(authConfigProperties.getEncodeKey().getBytes()));
            String password = aes.decryptStr(loginModel.getPassword());
            loginModel.setPassword(password);
        }

        // 获取当前已经认证的客户端信息
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ErrorCodes.INVALID_CLIENT,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        Set<String> scope = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(loginModel.getScope(), " ")));

        // 创建token
        return new PlatAuthenticationToken(loginModel, new AuthorizationGrantType(SecurityConstants.PLAT),
                clientPrincipal, scope, loginModel.getAdditionalParameters());

    }
}
