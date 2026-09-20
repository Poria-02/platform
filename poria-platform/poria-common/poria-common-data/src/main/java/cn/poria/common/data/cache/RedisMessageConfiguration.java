package cn.poria.common.data.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis 发布/订阅监听容器的兜底配置。
 *
 * <p>业务模块通过构造器注入 {@link RedisMessageListenerContainer} 并注册各自的
 * Topic 监听器。若未引入事件模块且没有其他模块提供该容器，则创建一个共享容器。</p>
 */
@Configuration(proxyBeanMethods = false)
public class RedisMessageConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("cn.poria.common.event.config.EventAutoConfiguration")
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }
}
