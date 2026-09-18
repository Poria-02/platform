

package cn.poria.common.gateway.support;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.dto.RouteDto;
import cn.poria.common.gateway.vo.RouteDefinitionVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shanxincd
 * @date 2019-08-16
 * <p>
 * 路由缓存工具类
 */
@UtilityClass
@Slf4j
public class RouteCacheHolder {

	private Cache<String, RouteDefinitionVo> cache = CacheUtil.newLFUCache(200);

	/**
	 * 获取缓存的全部对象
	 * @return routeList
	 */
	public List<RouteDefinitionVo> getRouteList() {
		List<RouteDefinitionVo> routeList = new ArrayList<>();
		cache.forEach(route -> routeList.add(route));
		return routeList;
	}

	/**
	 * 更新缓存
	 * @param routeList 缓存列表
	 */
	public  List<RouteDefinitionVo> setRouteList(List<RouteDto> routeList) {

		log.info("转换路由:{}",JSONUtil.toJsonStr(routeList));
		routeList.forEach(route -> {

			RouteDefinitionVo vo = new RouteDefinitionVo();
			vo.setId(route.getRouteId());
			vo.setRouteName(route.getRouteName());
			JSONArray predicatesArray = JSONUtil.parseArray(route.getPredicates());
			List<PredicateDefinition> predicateDefinitionList = predicatesArray.toList(PredicateDefinition.class);
			JSONArray filtersArray = JSONUtil.parseArray( route.getFilters());
			List<FilterDefinition> filterDefinitionList = filtersArray.toList(FilterDefinition.class);
			vo.setPredicates(predicateDefinitionList);
			vo.setFilters(filterDefinitionList);
			vo.setUri(URI.create(route.getUri()));
			vo.setOrder( route.getOrder());
			cache.put(vo.getId(), vo);
		});

		return getRouteList();
	}

	/**
	 * 清空缓存
	 */
	public void removeRouteList() {
		cache.clear();
	}

}
