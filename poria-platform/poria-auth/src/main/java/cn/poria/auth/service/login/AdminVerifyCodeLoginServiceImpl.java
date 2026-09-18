package cn.poria.auth.service.login;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.poria.auth.config.AuthConfigProperties;
import cn.poria.auth.service.VerifyCodeService;
import cn.poria.auth.utils.PwdLockUtil;
import cn.poria.common.core.constant.CommonConstants;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.security.constant.PlatLoginType;
import cn.poria.common.security.service.PlatAuthenticationToken;
import cn.poria.common.security.service.PlatUser;
import cn.poria.common.security.service.PlatUserLoginService;
import cn.poria.common.security.util.AuthConstant;
import cn.poria.upms.api.dto.UserInfo;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.feign.RemoteUserService;
import cn.poria.upms.api.util.ParamResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * author qiaodi
 * date 2025/12/4 09:37
 * version 6.8.1
 * description
 */
@Slf4j
@Service(PlatUserLoginService.SERVICE_ID + PlatLoginType.ADMIN_VERIFY_CODE)
public class AdminVerifyCodeLoginServiceImpl implements PlatUserLoginService {

    @Autowired
    private VerifyCodeService verifyCodeService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private AuthConfigProperties authConfigProperties;

    @Override
    public UserDetails loadUser(PlatAuthenticationToken token, boolean checkVerifyCode) {

        //用户名解密
        if(authConfigProperties.enable && StrUtil.isNotBlank(token.getLoginModel().getUsername())){
            AES aes = new AES(Mode.CFB, Padding.NoPadding,
                    new SecretKeySpec(authConfigProperties.getEncodeKey().getBytes(), "AES"),
                    new IvParameterSpec(authConfigProperties.getEncodeKey().getBytes()));
            String username = aes.decryptStr(token.getLoginModel().getUsername());
            token.getLoginModel().setUsername(username);
        }

        //校验验证码
        if(checkVerifyCode){
            verifyCodeService.checkAdminSmsVerifyCode(token.getLoginModel().getUsername(), token.getLoginModel().getUserType(), token.getLoginModel().getVerifyCode());
        }

        return getUserDetails(token, remoteUserService.infoByMobile(token.getLoginModel().getUsername(),token.getLoginModel().getUserType(), SecurityConstants.FROM_IN));
    }

    private UserDetails getUserDetails(PlatAuthenticationToken token, R<UserInfo> result) {

        //用户是否存在
        if (result == null || result.getData() == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        //校验封停状态
        if (result.getCode() == 2) {
            throw new LockedException("用户名或密码错误");
        }

        UserInfo info = result.getData();

        Set<String> dbAuthsSet = new HashSet<>();
        Set<String> roleIds = new HashSet<>();
        if (ArrayUtil.isNotEmpty(info.getRoles())) {
            //获取角色权限
            Arrays.stream(info.getRoles()).forEach(roleId -> {
                dbAuthsSet.add(SecurityConstants.ROLE + roleId);
                roleIds.add(String.valueOf(roleId));
            });

            //获取资源权限
            dbAuthsSet.addAll(Arrays.asList(info.getPermissions()));
        }

        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();
        boolean enabled = StrUtil.equals(user.getLockFlag(), CommonConstants.STATUS_NORMAL);


        return new PlatUser(user.getUserId().toString(), user.getDeptId(), user.getPhone(), user.getAvatar(), user.getUserId().toString(),
                user.getUsername(), new BCryptPasswordEncoder().encode("123456"), token.getLoginModel().getLoginType(), enabled, true, true,
                !CommonConstants.STATUS_LOCK.equals(user.getLockFlag()), authorities,roleIds);
    }
}
