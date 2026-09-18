package cn.poria.common.security.xss.core;

import cn.poria.common.security.xss.config.MicaXssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * jackson xss 处理
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class JacksonXssClean extends XssCleanDeserializerBase {
	private final MicaXssProperties properties;
	private final XssCleaner xssCleaner;

	@Override
	public String clean(String name, String text) throws IOException {
		if (text == null) {
			return null;
		}
		// 判断是否忽略
		if (XssHolder.isIgnore(name)) {
			return text;
		}
		String value = xssCleaner.clean(name, text, XssType.JACKSON);
		MicaXssProperties.JacksonConfig jackson = properties.getJackson();
		String charsToDelete = jackson.getCharsToDelete();
		if (!charsToDelete.isEmpty()) {
			value = StringUtils.deleteAny(value, charsToDelete);
		}
		boolean isTrimText = properties.isTrimText() || jackson.isTrimText();
		if (isTrimText) {
			value = value.trim();
		}
		boolean emptyAsNull = jackson.isEmptyAsNull();
		if (emptyAsNull && value.isEmpty()) {
			value = null;
		}
		log.debug("Json property name:{} value:{} cleaned up by mica-xss, current value is:{}.", name, text, value);
		return value;
	}

}
