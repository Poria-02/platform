package cn.poria.common.security.xss.core;

import cn.poria.common.security.xss.config.MicaXssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * 表单 xss 处理
 *
 * @author L.cm
 */
@AutoIgnore
@ControllerAdvice
@ConditionalOnProperty(
	prefix = MicaXssProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
@RequiredArgsConstructor
public class FormXssClean {
	private final MicaXssProperties properties;
	private final XssCleaner xssCleaner;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// 处理前端传来的表单字符串
		MicaXssProperties.FormConfig form = properties.getForm();
		binder.registerCustomEditor(String.class, new StringPropertiesEditor(xssCleaner, properties));
		if (form.isEnableInCollection()) {
			binder.registerCustomEditor(Collection.class, new CustomCollectionEditor(Collection.class));
			binder.registerCustomEditor(Set.class, new CustomCollectionEditor(Set.class));
			binder.registerCustomEditor(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
			binder.registerCustomEditor(List.class, new CustomCollectionEditor(List.class));
		}
	}

	@Slf4j
	public static class StringPropertiesEditor extends PropertyEditorSupport {
		public StringPropertiesEditor(XssCleaner xssCleaner, MicaXssProperties properties) {
			this.xssCleaner = xssCleaner;
			this.properties = properties;
		}

		private final XssCleaner xssCleaner;
		private final MicaXssProperties properties;

		@Override
		public void setAsText(String text) {
			if (text == null) {
				setValue(null);
			} else {
				if (!XssHolder.isEnabled()) {
					setValue(text);
					return;
				}
				String value = xssCleaner.clean(text, XssType.FORM);
				MicaXssProperties.FormConfig form = properties.getForm();
				String charsToDelete = form.getCharsToDelete();
				if (!charsToDelete.isEmpty()) {
					value = StringUtils.deleteAny(value, charsToDelete);
				}
				boolean isTrimText = properties.isTrimText() || form.isTrimText();
				if (isTrimText) {
					value = value.trim();
				}
				boolean emptyAsNull = form.isEmptyAsNull();
				if (emptyAsNull && value.isEmpty()) {
					value = null;
				}
				setValue(value);
				log.debug("Request parameter value:{} cleaned up by mica-xss, current value is:{}.", text, value);
			}
		}
	}

}
