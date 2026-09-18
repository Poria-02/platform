package cn.poria.common.security.xss.core;

import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 利用 ThreadLocal 缓存线程间的数据
 *
 * @author L.cm
 */
@UtilityClass
public class XssHolder {
	private static final ThreadLocal<XssIgnoreVo> TL = new ThreadLocal<>();

	/**
	 * 是否开启
	 *
	 * @return boolean
	 */
	public static boolean isEnabled() {
		return Objects.isNull(TL.get());
	}

	/**
	 * 判断是否被忽略
	 *
	 * @return XssCleanIgnore
	 */
	static boolean isIgnore(String name) {
		XssIgnoreVo cleanIgnore = TL.get();
		if (cleanIgnore == null) {
			return false;
		}
		String[] ignoreArray = cleanIgnore.getNames();
		// 1. 如果没有设置忽略的字段
		if (ignoreArray.length == 0) {
			return true;
		}
		// 2. 指定忽略的属性
		return ObjectUtils.containsElement(ignoreArray, name);
	}

	/**
	 * 标记为开启
	 */
	static void setIgnore(XssIgnoreVo ignoreVo) {
		TL.set(ignoreVo);
	}

	/**
	 * 关闭 xss 清理
	 */
	public static void remove() {
		TL.remove();
	}

}
