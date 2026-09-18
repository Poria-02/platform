package cn.poria.common.security.xss.core;

import cn.poria.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

/**
 * xss 数据处理类型
 */
@Slf4j
public enum XssType {

	/**
	 * 表单
	 */
	FORM() {
		@Override
		public RuntimeException getXssException(String name, String input, String message) {
			return new ServiceException( message);
		}
	},

	/**
	 * body json
	 */
	JACKSON() {
		@Override
		public RuntimeException getXssException(String name, String input, String message) {
			return new ServiceException(message);
		}
	};

	/**
	 * 获取 xss 异常
	 *
	 * @param name    属性名
	 * @param input   input
	 * @param message message
	 * @return XssException
	 */
	public abstract RuntimeException getXssException(String name, String input, String message);

}
