package cn.poria.auth.service.login;

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
import cn.poria.upms.api.dto.UserInfo;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.feign.RemoteUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 运营端用户登陆
 *
 * @author zhangchunlei
 * @date 2021/6/7 5:15 下午
 */
@Service(PlatUserLoginService.SERVICE_ID + PlatLoginType.ADMIN_PASSWORD)
@Slf4j
public class AdminPwdLoginServiceImpl implements PlatUserLoginService {

	@Resource
    private RemoteUserService remoteUserService;

	@Resource
    private VerifyCodeService verifyCodeService;

    @Resource
    private PwdLockUtil pwdLockUtil;

	@Resource
    private AuthConfigProperties authConfigProperties;

    @Override
    public UserDetails loadUser(PlatAuthenticationToken token, boolean checkVerifyCode) {

        //校验验证码
        if (checkVerifyCode) {
            verifyCodeService.checkVerifyCode(token.getLoginModel().getRandomStr(), token.getLoginModel().getVerifyCode());
        }

        //用户名解密
        if (authConfigProperties.enable && StrUtil.isNotBlank(token.getLoginModel().getUsername())) {
            AES aes = new AES(Mode.CFB, Padding.NoPadding,
                    new SecretKeySpec(authConfigProperties.getEncodeKey().getBytes(), "AES"),
                    new IvParameterSpec(authConfigProperties.getEncodeKey().getBytes()));
            String username = aes.decryptStr(token.getLoginModel().getUsername());
            token.getLoginModel().setUsername(username);
        }
        log.info("token参数:{}", token);
//		R<UserInfo> result = remoteUserService.info(token.getLoginModel().getUsername(),token.getLoginModel().getUserType(), SecurityConstants.FROM_IN);
		R<UserInfo> result = remoteUserService.info(token.getLoginModel().getUsername(), SecurityConstants.FROM_IN);
        return getUserDetails(token,result);
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

        if (CommonConstants.STATUS_LOCK.equals(info.getSysUser().getLockFlag())) {
            throw new LockedException("用户名或密码错误");
        }


        //前置校验是否锁定账号
//		pwdLockUtil.preProcessing(AuthConstant.ADMIN_PWD_LIMIT_KEY + info.getSysUser().getUserId());

        //后置校验用户密码输入次数
//		pwdLockUtil.postProcessing(token.getLoginModel().getPassword(),info.getSysUser().getPassword(), AuthConstant.ADMIN_PWD_LIMIT_KEY + info.getSysUser().getUserId());


//		//3个月修改密码
//		if(DateUtil.offsetDay(info.getSysUser().getLastPwdUpdateTime(),Integer.parseInt(ParamResolver.getStr("PWD_EXPIRE_DAY"))).before(new Date())){
//			throw new CredentialsExpiredException("账号密码已过期，请修改");
//		}

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
                user.getUsername(), user.getPassword(), token.getLoginModel().getLoginType(), enabled, true, true,
                !CommonConstants.STATUS_LOCK.equals(user.getLockFlag()), authorities, roleIds);
    }
}
