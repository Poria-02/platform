package cn.poria.upms.config;

import cn.poria.common.core.dto.RouteDto;
import cn.poria.common.core.event.DynamicRouteInitEvent;
import cn.poria.upms.service.SysRouteConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

@Configuration
public class DynamicRouteInitRunner implements InitializingBean {
   private static final Logger log = LoggerFactory.getLogger(DynamicRouteInitRunner.class);
   private final RedisTemplate redisTemplate;
   private final SysRouteConfService routeConfService;
   private final RedisMessageListenerContainer listenerContainer;

   @Async
   @Order
   @EventListener({WebServerInitializedEvent.class})
   public void WebServerInit() {
      this.initRoute();
   }

   @Async
   @Order
   @TransactionalEventListener({DynamicRouteInitEvent.class})
   public void initRoute() {
      this.redisTemplate.delete("gateway_route_key");
      log.info("开始初始化网关路由");
      this.routeConfService.list().forEach((route) -> {
         RouteDto routeDto = new RouteDto();
         BeanUtils.copyProperties(route, routeDto);
         log.info("加载路由ID：{},{}", route.getRouteId(), routeDto);
         this.redisTemplate.setKeySerializer(new StringRedisSerializer());
         this.redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(RouteDto.class));
         this.redisTemplate.opsForHash().put("gateway_route_key", route.getRouteId(), routeDto);
      });
      this.redisTemplate.convertAndSend("gateway_jvm_route_reload_topic", "路由信息,网关缓存更新");
      log.info("初始化网关路由结束 ");
   }

   public void afterPropertiesSet() {
      this.listenerContainer.addMessageListener((message, bytes) -> {
         log.warn("接收到重新Redis 重新加载路由事件");
         this.initRoute();
      }, new ChannelTopic("gateway_redis_route_reload_topic"));
   }

   public DynamicRouteInitRunner(final RedisTemplate redisTemplate, final SysRouteConfService routeConfService, final RedisMessageListenerContainer listenerContainer) {
      this.redisTemplate = redisTemplate;
      this.routeConfService = routeConfService;
      this.listenerContainer = listenerContainer;
   }
}
