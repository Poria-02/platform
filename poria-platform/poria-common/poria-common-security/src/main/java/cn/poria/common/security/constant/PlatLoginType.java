package cn.poria.common.security.constant;

/**
 * @author zhangchunlei
 * @date 2021年06月05日 12:03 下午
 */

public interface PlatLoginType {

	String ADMIN_PASSWORD = "ADMIN_PWD";

	String USER_PASSWORD = "USER_PWD";

	/**
	 * 管理后台手机号验证码登录
	 */
	String ADMIN_VERIFY_CODE = "ADMIN_VERIFY_CODE";

	String PLAT_INNER_DEFAULT = "PLAT_INNER_DEFAULT";

	/**
	 * 小程序登陆
	 */
	String MA_USER = "MA_USER";

	/**
	 * 小程序内部自动登陆使用
	 */
	String MA_USER_INNER = "MA_USER_INNER";

}
