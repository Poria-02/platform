package cn.poria.gateway.config;

import cn.poria.gateway.filter.PasswordDecoderFilter;
import cn.poria.gateway.filter.PlatRequestGlobalFilter;
import cn.poria.gateway.handler.GlobalExceptionHandler;
import cn.poria.gateway.handler.ImageCodeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 网关配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GatewayConfigProperties.class)
public class GatewayConfiguration {

	@Bean
	public PasswordDecoderFilter passwordDecoderFilter(GatewayConfigProperties configProperties) {
		return new PasswordDecoderFilter(configProperties);
	}

	@Bean
	public PlatRequestGlobalFilter pigRequestGlobalFilter() {
		return new PlatRequestGlobalFilter();
	}

	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalExceptionHandler(objectMapper);
	}

	@Bean
	public ImageCodeHandler imageCodeHandler(StringRedisTemplate redisTemplate) {
		return new ImageCodeHandler(redisTemplate);
	}

}
