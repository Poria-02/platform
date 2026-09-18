package cn.poria.common.security.xss.core;

/**
 * xss 清理器
 *
 * @author L.cm
 */
public interface XssCleaner {

	/**
	 * 清理 html
	 *
	 * @param value 属性值
	 * @param type  XssType
	 * @return 清理后的数据
	 */
	default String clean(String value, XssType type) {
		return clean(null, value, type);
	}

	/**
	 * 清理 html
	 *
	 * @param name  属性名
	 * @param value 属性值
	 * @param type  XssType
	 * @return 清理后的数据
	 */
	String clean(String name, String value, XssType type);

}
