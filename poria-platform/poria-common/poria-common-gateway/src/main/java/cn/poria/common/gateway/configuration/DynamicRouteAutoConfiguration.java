package cn.poria.common.gateway.configuration;

import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.dto.RouteDto;
import cn.poria.common.core.util.SpringContextHolder;
import cn.poria.common.gateway.support.RouteCacheHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;


/**
 * 动态路由配置类
 */
@Slf4j
@Configuration
@ComponentScan("cn.poria.common.gateway")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class DynamicRouteAutoConfiguration {

	@Bean
	public ReactiveRedisTemplate<String, RouteDto> routeRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
		Jackson2JsonRedisSerializer<RouteDto> routeSerializer = new Jackson2JsonRedisSerializer<>(RouteDto.class);
		RedisSerializationContext<String, RouteDto> serializationContext = RedisSerializationContext
				.<String, RouteDto>newSerializationContext(RedisSerializer.string())
				.value(routeSerializer)
				.hashKey(RedisSerializer.string())
				.hashValue(routeSerializer)
				.build();
		return new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
	}

	/**
	 * redis 监听配置
	 * @param redisConnectionFactory redis 配置
	 * @return
	 */
	@Bean
	public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener((message, bytes) -> {
			log.info("接收到重新JVM 重新加载路由事件");
			RouteCacheHolder.removeRouteList();
			// 发送刷新路由事件
			SpringContextHolder.publishEvent(new RefreshRoutesEvent(this));
		}, new ChannelTopic(CacheConstants.ROUTE_JVM_RELOAD_TOPIC));
		return container;
	}

}
