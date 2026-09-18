package cn.poria.common.data.cache;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * RedisTemplate 配置
 *
 * @author L.cm
 */
@EnableCaching
@Configuration
@AutoConfigureBefore(name = { "org.redisson.spring.starter.RedissonAutoConfigurationV2",
		"org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration" })
public class RedisTemplateConfiguration {

	@Bean
	@Primary
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setHashKeySerializer(RedisSerializer.string());
		redisTemplate.setValueSerializer(RedisSerializer.java());
		redisTemplate.setHashValueSerializer(RedisSerializer.java());
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

}
