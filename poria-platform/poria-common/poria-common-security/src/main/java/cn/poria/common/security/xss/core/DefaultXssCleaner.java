package cn.poria.common.security.xss.core;

import cn.hutool.http.HtmlUtil;
import cn.poria.common.security.xss.config.MicaXssProperties;
import cn.poria.common.security.xss.config.MicaXssProperties.Mode;
import cn.poria.common.security.xss.utils.XssFilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

/**
 * 默认的 xss 清理器
 *
 * @author L.cm
 */
@Slf4j
public class DefaultXssCleaner implements XssCleaner {
	private final MicaXssProperties properties;

	public DefaultXssCleaner(MicaXssProperties properties) {
		this.properties = properties;
	}

	@Override
	public String clean(String name, String bodyHtml, XssType type) {
		// 为空直接返回
		if (!StringUtils.hasText(bodyHtml)) {
			return bodyHtml;
		}
		Mode mode = properties.getMode();
		if (Mode.ESCAPE == mode) {
			// html 转义
			return HtmlUtils.htmlEscape(bodyHtml, StandardCharsets.UTF_8.name());
		} else if (Mode.VALIDATE == mode) {
			// 校验
			if (!XssFilterUtil.containsXss(bodyHtml)) {
				return bodyHtml;
			}
			throw type.getXssException(name, bodyHtml, "Xss validate fail, input value:" + bodyHtml);
		} else {
			// 清理后的 html
			String filter = XssFilterUtil.filterXss(bodyHtml);
			if (properties.isEnableEscape()) {
				return HtmlUtil.escape(filter);
			}
			return filter;
		}
	}

}
