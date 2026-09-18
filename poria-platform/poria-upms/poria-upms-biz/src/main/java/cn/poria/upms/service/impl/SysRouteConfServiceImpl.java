package cn.poria.upms.service.impl;

import cn.hutool.json.JSONUtil;
import cn.poria.common.core.dto.RouteDto;
import cn.poria.common.core.event.DynamicRouteInitEvent;
import cn.poria.upms.api.entity.SysRouteConf;
import cn.poria.upms.api.vo.RouteVo;
import cn.poria.upms.mapper.SysRouteConfMapper;
import cn.poria.upms.service.SysRouteConfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class SysRouteConfServiceImpl extends ServiceImpl<SysRouteConfMapper, SysRouteConf> implements SysRouteConfService {
   private static final Logger log = LoggerFactory.getLogger(SysRouteConfServiceImpl.class);
   private final RedisTemplate redisTemplate;
   private final ApplicationEventPublisher applicationEventPublisher;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public Mono<Void> refresh() {
      Boolean result = this.redisTemplate.delete("gateway_route_key");
      log.info("清空网关路由 {} ", result);
      List<SysRouteConf> routeConfList = this.list();

      try {
         routeConfList.forEach((value) -> {
            RouteDto routeDto = new RouteDto();
            BeanUtils.copyProperties(value, routeDto);
            log.info("加载路由：{}", JSONUtil.toJsonStr(routeDto));
            this.redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(RouteDto.class));
            this.redisTemplate.opsForHash().put("gateway_route_key", routeDto.getRouteId(), routeDto);
         });
         log.debug("更新网关路由结束 ");
         this.redisTemplate.convertAndSend("gateway_jvm_route_reload_topic", "UPMS路由信息,网关缓存更新");
      } catch (Exception e) {
         log.error("路由配置解析失败", e);
         this.applicationEventPublisher.publishEvent(new DynamicRouteInitEvent(this));
         throw new RuntimeException(e);
      }

      return Mono.empty();
   }

   public List<RouteVo> queryList() {
      List<RouteVo> routeVoList = (List)this.list().stream().map((sysRouteConf) -> {
         RouteVo routeVo = new RouteVo();
         BeanUtils.copyProperties(sysRouteConf, routeVo);
         return routeVo;
      }).collect(Collectors.toList());
      return routeVoList;
   }

   public SysRouteConfServiceImpl(final RedisTemplate redisTemplate, final ApplicationEventPublisher applicationEventPublisher) {
      this.redisTemplate = redisTemplate;
      this.applicationEventPublisher = applicationEventPublisher;
   }
}
