package cn.poria.upms.config;

import cn.hutool.core.util.StrUtil;
import cn.poria.upms.service.SysOauthClientDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class ClientDetailsInitRunner implements InitializingBean {
   private static final Logger log = LoggerFactory.getLogger(ClientDetailsInitRunner.class);
   private final SysOauthClientDetailsService clientDetailsService;
   private final RedisMessageListenerContainer listenerContainer;
   private final RedisTemplate redisTemplate;

   @Async
   @Order
   @EventListener({WebServerInitializedEvent.class})
   public void WebServerInit() {
      this.initClientDetails();
   }

   @Async
   @Order
   @TransactionalEventListener({ClientDetailsInitEvent.class})
   public void initClientDetails() {
      log.debug("初始化客户端信息开始 ");
      this.clientDetailsService.list().stream().filter((client) -> StrUtil.isNotBlank(client.getAdditionalInformation())).forEach((client) -> {
         String key = String.format("%s:%s", "client_config_flag", client.getClientId());
         this.redisTemplate.opsForValue().set(key, client.getAdditionalInformation());
         log.debug("初始化客户端信息结束 ");
      });
   }

   public void afterPropertiesSet() throws Exception {
      this.listenerContainer.addMessageListener((message, bytes) -> {
         log.warn("接收到重新Redis 重新加载客户端配置事件");
         this.initClientDetails();
      }, new ChannelTopic("upms_redis_client_reload_topic"));
   }

   public ClientDetailsInitRunner(final SysOauthClientDetailsService clientDetailsService, final RedisMessageListenerContainer listenerContainer, final RedisTemplate redisTemplate) {
      this.clientDetailsService = clientDetailsService;
      this.listenerContainer = listenerContainer;
      this.redisTemplate = redisTemplate;
   }

   public static class ClientDetailsInitEvent extends ApplicationEvent {
      public ClientDetailsInitEvent(Object source) {
         super(source);
      }
   }
}
