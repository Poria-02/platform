package cn.poria.common.security.xss.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 忽略存储
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public class XssIgnoreVo {

	/**
	 * 跳过的属性名
	 */
	private final String[] names;

	public XssIgnoreVo() {
		this(new String[0]);
	}
}
