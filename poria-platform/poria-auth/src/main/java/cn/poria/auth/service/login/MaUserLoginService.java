//package cn.poria.auth.service.login;
//
//import cn.hutool.core.util.StrUtil;
//import cn.poria.auth.service.feign.RemoteCustomerService;
//import cn.poria.auth.service.feign.RemoteWxMaService;
//import cn.poria.common.core.constant.SecurityConstants;
//import cn.poria.common.core.util.R;
//import cn.poria.common.security.constant.PlatLoginType;
//import cn.poria.common.security.service.PlatAuthenticationToken;
//import cn.poria.common.security.service.PlatUser;
//import cn.poria.common.security.service.PlatUserLoginService;
//import cn.poria.customer.enums.ThirdLoginTypeEnum;
//import cn.poria.customer.vo.response.LoginUserAccountVo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author zhangchunlei
// * @date 2021年07月12日 2:15 下午
// */
//@Slf4j
//@Service(PlatUserLoginService.SERVICE_ID + PlatLoginType.MA_USER)
//public class MaUserLoginService implements PlatUserLoginService {
//
//	@Autowired
//	private RemoteWxMaService remoteMaService;
//
//	@Autowired
//	private RemoteCustomerService remoteCustomerService;
//	@Autowired
//	private ImService imService;
//
//
//	@Override
//	public UserDetails loadUser(PlatAuthenticationToken token, boolean checkVerifyCode) {
//
//		R<String> r = remoteMaService.queryOpenId(token.getLoginModel().getAppId(), token.getLoginModel().getUsername(), SecurityConstants.FROM_IN);
//		if(!r.isSuccess()){
//			throw new AuthenticationServiceException(r.getMsg());
//		}
//		try{
//			R<LoginUserAccountVo> user = remoteCustomerService.loadUserByOpenId(r.getData(),
//					ThirdLoginTypeEnum.THIRD_TYPE_MA.getType(),SecurityConstants.FROM_IN);
//
//			return getCustomerDetails(user,token.getLoginModel().getLoginType());
//		}catch (UsernameNotFoundException e){
//			throw e;
//		}catch (Exception e){
//			log.error("登陆失败",e);
//			throw new AuthenticationServiceException(e.getLocalizedMessage());
//		}
//	}
//
//	/**
//	 * C端用户登录
//	 * 构建 UserDetails
//	 * @param userAcc 客户详情
//	 */
//	private UserDetails getCustomerDetails(R<LoginUserAccountVo> userAcc,String loginType) {
//		if (userAcc == null || userAcc.getData() == null) {
//			throw new UsernameNotFoundException("用户不存在");
//		}
//
//		//获取用户信息
//		LoginUserAccountVo user = userAcc.getData();
//		log.info("RemoteCustomerService 返回 {}, {}, {}", user.getMobile(), user.getId(), user.getName());
//
//		//权限,模拟设置权限
//		Set<String> authsSet = new HashSet<>();
//		authsSet.add("Customer");
//		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authsSet.toArray(new String[0]));
//
//		//构造security用户
//		PlatUser platUser = new PlatUser(user.getId(), Long.valueOf(user.getOrgId()), user.getMobile(), user.getHeadPath() , user.getId(), user.getName(),
//				new BCryptPasswordEncoder().encode("123456"),loginType,true, true, true,
//				user.getStatus().equals(0), authorities);
//
//		//获取网易云信Token
//		appendYXToken(user);
//		platUser.setImToken(user.getImToken());
//		return platUser;
//	}
//
//	//获取网易云信Token，C端用户
//	private void appendYXToken(LoginUserAccountVo user) {
//		//Token已经存在
//		if (StrUtil.isNotBlank(user.getImToken())) {
//			return;
//		}
//
//		//获取云信Token
//		String token = imService.imRegister(user.getId(), user.getName(), user.getHeadPath());
//		if (StrUtil.isBlank(token)) {
//			return;
//		}
//
//		//保存云信Token
//		user.setImToken(token);
//		remoteCustomerService.saveImToken(user.getId(), token, SecurityConstants.FROM_IN);
//	}
//
//}
