//package cn.poria.auth.service.login;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import cn.poria.auth.service.feign.RemoteMessageService;
//import cn.poria.common.core.constant.SecurityConstants;
//import cn.poria.common.core.util.R;
//import cn.poria.message.vo.request.im.IMRegister;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * @author zhangchunlei
// * @date 2021年12月29日 10:24 上午
// */
//@Slf4j
//@Service
//public class ImService {
//
//	@Autowired
//	private RemoteMessageService remoteMessageService;
//
//
//	public String imRegister(String userId, String name, String head){
//		if (StrUtil.isBlank(userId)) {
//			return null;
//		}
//
//		//头像裁剪
//		if (StrUtil.isNotBlank(head)) {
//			if (head.contains("?")) {
//				head = head + "&x-oss-process=image/resize,m_fill,h_200,w_200";
//			}
//			else {
//				head = head + "?x-oss-process=image/resize,m_fill,h_200,w_200";
//			}
//		}
//
//		//优先通过ID注册
//		R<String> r = remoteMessageService.registerIMAccount(IMRegister.builder().accountId(userId).name(name).faceUrl(head).build(),
//				SecurityConstants.FROM_IN);
//		log.info("账户注册IM结果:{}", JSONUtil.toJsonStr(r));
//		if(r.isSuccess()){
//			return r.getData();
//		}
//		return null;
//	}
//
//}
