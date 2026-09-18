package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysRouteConf;
import cn.poria.upms.api.vo.RouteVo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import reactor.core.publisher.Mono;

public interface SysRouteConfService extends IService<SysRouteConf> {
   Mono<Void> refresh();

   List<RouteVo> queryList();
}
