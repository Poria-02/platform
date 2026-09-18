package cn.poria.auth.support.plat;

import cn.hutool.json.JSONUtil;
import cn.poria.auth.utils.OAuth2ErrorCodesExpand;
import cn.poria.common.core.util.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PlatAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());
            log.error("登录失败:", exception);
		    R r = null;
            if(exception instanceof OAuth2AuthenticationException){
                String errorCode = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
                switch (errorCode){
                    case OAuth2ErrorCodesExpand.USERNAME_NOT_FOUND :
                        r = R.builder().code(LoginErrorCodeConstant.USER_NOT_EXIST).msg(exception.getLocalizedMessage()).build();
                        break;
                    case OAuth2ErrorCodesExpand.CREDENTIALS_EXPIRED:
                        r = R.builder().code(LoginErrorCodeConstant.ACCOUNT_PWD_EXPIRE).msg(exception.getLocalizedMessage()).build();
                        break;
                    default:
                        r = R.builder().code(LoginErrorCodeConstant.ERROR).msg(exception.getLocalizedMessage()).build();
                        break;
                }
			}else {
				r = R.builder().code(LoginErrorCodeConstant.ERROR).msg(exception.getLocalizedMessage()).build();
			}

            response.getWriter().write(JSONUtil.toJsonStr(r));

    }
}
