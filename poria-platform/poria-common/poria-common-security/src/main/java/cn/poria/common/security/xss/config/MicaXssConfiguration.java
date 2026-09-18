package cn.poria.common.security.xss.config;

import cn.poria.common.security.xss.core.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * jackson xss 配置
 *
 * @author L.cm
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(MicaXssProperties.class)
@ConditionalOnProperty(
	prefix = MicaXssProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MicaXssConfiguration implements WebMvcConfigurer {
	private final MicaXssProperties xssProperties;

	@Bean
	@ConditionalOnMissingBean
	public XssCleaner xssCleaner(MicaXssProperties properties) {
		return new DefaultXssCleaner(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public FormXssClean formXssClean(MicaXssProperties properties,
									 XssCleaner xssCleaner) {
		return new FormXssClean(properties, xssCleaner);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(MicaXssProperties properties,
																	  XssCleaner xssCleaner) {
		return builder -> builder.deserializerByType(String.class, new JacksonXssClean(properties, xssCleaner));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> patterns = new ArrayList<>();
		// 拦截路由和排除的路由
		patterns.addAll(xssProperties.getPathPatterns());
		patterns.addAll(xssProperties.getPathExcludePatterns());
		if (patterns.isEmpty()) {
			patterns.add("/**");
		}
		// 拦截所有
		XssCleanInterceptor interceptor = new XssCleanInterceptor(xssProperties);
		registry.addInterceptor(interceptor)
			.addPathPatterns(patterns)
			.order(Ordered.LOWEST_PRECEDENCE);
	}

}
