

package cn.poria.base.oss;

import cn.poria.base.oss.http.OssEndpoint;
import cn.poria.base.oss.service.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * aws 自动配置类
 *
 * @author shanxincd
 * @author 858695266
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ OssProperties.class })
public class OssAutoConfiguration {

	private final OssProperties properties;

	@Bean
	@ConditionalOnMissingBean(OssTemplate.class)
	@ConditionalOnProperty(name = "oss.enable", havingValue = "true", matchIfMissing = true)
	public OssTemplate ossTemplate() {
		return new OssTemplate(properties);
	}

	@Bean
	@ConditionalOnProperty(name = "oss.info", havingValue = "true")
	public OssEndpoint ossEndpoint(OssTemplate template) {
		return new OssEndpoint(template);
	}

}

