package cn.poria.common.security.service;

import cn.poria.common.core.exception.ServiceException;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * @date 2021年06月05日 10:13 上午
 */
public class PlatUserServiceContext {

    @Resource
    private Map<String, PlatUserLoginService> userLoginServiceContext;

    public UserDetails loadUser(Authentication authentication) {

        PlatAuthenticationToken token = (PlatAuthenticationToken) authentication;
        return loadUser(token, true);
    }


    public UserDetails loadUser(PlatAuthenticationToken token, boolean checkVerifyCode) {
        PlatUserLoginService userLoginService = userLoginServiceContext.get(PlatUserLoginService.SERVICE_ID + token.getLoginModel().getLoginType());
        if (userLoginService != null) {
            return userLoginService.loadUser(token, checkVerifyCode);
        }
        throw new ServiceException("不支持的登陆方式");
    }

}
