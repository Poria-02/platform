

package cn.poria.common.gateway.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.dto.RouteDto;
import cn.poria.common.core.util.SpringContextHolder;
import cn.poria.common.gateway.vo.RouteDefinitionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2018/10/31
 * <p>
 * redis 保存路由信息，优先级比配置文件高
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRouteDefinitionWriter implements RouteDefinitionRepository {

	private final ReactiveRedisTemplate<String, RouteDto> routeRedisTemplate;

	private final ReactiveStringRedisTemplate redisTemplate;

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return route.flatMap(r -> {
			RouteDto routeDto = new RouteDto();
			routeDto.setRouteId(r.getId());
			if (r instanceof RouteDefinitionVo vo) {
				routeDto.setRouteName(vo.getRouteName());
			}
			routeDto.setPredicates(JSONUtil.toJsonStr(r.getPredicates()));
			routeDto.setFilters(JSONUtil.toJsonStr(r.getFilters()));
			routeDto.setUri(r.getUri().toString());
			routeDto.setOrder(r.getOrder());
			routeDto.setMetadata(JSONUtil.toJsonStr(r.getMetadata()));
			log.info("保存路由信息{}", routeDto);
			return routeRedisTemplate.<String, RouteDto>opsForHash()
					.put(CacheConstants.ROUTE_KEY, r.getId(), routeDto)
					.then(redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "新增路由信息,网关缓存更新"))
					.then();
		});
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.flatMap(id -> {
			log.info("删除路由信息{}", id);
			return routeRedisTemplate.opsForHash().remove(CacheConstants.ROUTE_KEY, id)
					.then(redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "删除路由信息,网关缓存更新"))
					.then();
		});
	}

	/**
	 * 动态路由入口
	 * <p>
	 * 1. 先从内存中获取 2. 为空加载Redis中数据 3. 更新内存
	 * @return
	 */
	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		return Flux.defer(() -> {
			List<RouteDefinitionVo> routeList = RouteCacheHolder.getRouteList();
			if (CollUtil.isNotEmpty(routeList)) {
				log.debug("内存 中路由定义条数： {}， {}", routeList.size(), routeList);
				return Flux.fromIterable(routeList);
			}

			return routeRedisTemplate.<String, RouteDto>opsForHash().values(CacheConstants.ROUTE_KEY)
					.collectList()
					.flatMapMany(values -> {
						List<RouteDefinitionVo> routes = RouteCacheHolder.setRouteList(values);
						log.info("redis 中路由定义条数： {}， {}", values.size(), JSONUtil.toJsonStr(values));

						try {
							SwaggerUiConfigProperties swaggerUiConfigProperties = SpringContextHolder.getBean(SwaggerUiConfigProperties.class);

							swaggerUiConfigProperties.setUrls(routes.stream().flatMap(routeDefinition -> routeDefinition.getPredicates().stream()
								.filter(predicateDefinition -> "Path".equalsIgnoreCase(predicateDefinition.getName()))
								.map(predicateDefinition ->
										swaggerUrl(routeDefinition.getId() + "-" + routeDefinition.getRouteName(),
												predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0").replace("/**", "")
										)
								)).collect(Collectors.toSet()));
						}catch (Exception e){
							log.error("加载swagger路由失败:{}",e.getMessage());
						}

						log.info("刷新路由:{}", JSONUtil.toJsonStr(routes));
						return Flux.fromIterable(routes);
					});
		});
	}


	private AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl(String name , String path){
		AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
		swaggerUrl.setName(name);
		swaggerUrl.setUrl(String.format( "%s/v3/api-docs", path));
		return swaggerUrl;
	}

}
