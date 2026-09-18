package cn.poria.auth.service.login;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.security.constant.PlatLoginType;
import cn.poria.common.core.util.WebUtils;
import cn.poria.common.security.service.PlatAuthenticationToken;
import cn.poria.common.security.service.PlatUser;
import cn.poria.common.security.service.PlatUserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 返回一个默认不存在的用户token  只能用于系统使用
 */
@Service(PlatUserLoginService.SERVICE_ID + PlatLoginType.PLAT_INNER_DEFAULT)
public class PlatInnerDefaultLoginService implements PlatUserLoginService{
    @Override
    public UserDetails loadUser(PlatAuthenticationToken token, boolean checkVerifyCode) {

        //校验header
        HttpServletRequest request = WebUtils.getRequest();
        if(!StrUtil.equals("Y",request.getHeader("from"))){
            throw new BadCredentialsException("用户名或密码错误");
        }

        //权限,模拟设置权限
        Set<String> authsSet = new HashSet<>();
        authsSet.add("platDefault");
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authsSet.toArray(new String[0]));

       return new PlatUser("platDefault", null, "", "" , "platDefault", "platDefault",
                new BCryptPasswordEncoder().encode("123456"),token.getLoginModel().getLoginType(),
                true, true, true,
                true, authorities,null);
    }
}
