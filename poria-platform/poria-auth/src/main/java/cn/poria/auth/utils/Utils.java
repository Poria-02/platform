package cn.poria.auth.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @ClassName : Utils
 * @Description :
 * @Author : bruce
 * @Date : 2020-06-22 17:10
 */
public class Utils {

	public final static String D = "doctor";
	public final static String N = "nurse";
	public final static String U = "user";
	public final static String WX= "wx";//互联网医院大健康微信用户
	public final static String APPLE= "apple";//IOS用户

	public static final String DOCTOR_TYPE = "1005";
	public final static String PHONE = "phone"; //根据手机号直接登陆,不校验密码验证码

	public static String dict(String type) {
		if (type.equals(N)) {
			return "1006";
		}

		if (type.equals(D)) {
			return "1005";
		}

		return "2000";
	}
}
