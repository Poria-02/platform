package cn.poria.upms.controller;

import cn.hutool.json.JSONUtil;
import cn.poria.common.core.dto.RouteDto;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.upms.api.entity.SysRouteConf;
import cn.poria.upms.api.vo.RouteVo;
import cn.poria.upms.service.SysRouteConfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/route"})
@Tag(name = "route", description = "动态路由管理模块")
@Slf4j
public class SysRouteConfController {

    private final SysRouteConfService sysRouteConfService;

    @Operation(summary = "路由设置列表", description = "路由设置列表(sys_route_get)")
    @GetMapping({"list"})
    @PreAuthorize("@pms.hasPermission('sys_route_get')")
    public R<List<RouteVo>> queryList() {
        return R.ok(this.sysRouteConfService.queryList());
    }

    @Operation(summary = "路由设置详情", description = "路由设置详情(sys_route_detail)")
    @PreAuthorize("@pms.hasPermission('sys_route_detail')")
    @GetMapping({"/detail/{id}"})
    public R<RouteVo> getDetail(@PathVariable("id") Integer id) {
        SysRouteConf sysRouteConf = this.sysRouteConfService.getById(id);
        RouteVo routeVo = new RouteVo();
        if (null != sysRouteConf) {
            BeanUtils.copyProperties(sysRouteConf, routeVo);
        }

        return R.ok(routeVo);
    }

    @Operation(summary = "应用路由设置", description = "应用路由设置(sys_route_edit)")
    @PreAuthorize("@pms.hasPermission('sys_route_edit')")
    @PutMapping({"refresh"})
    public R refresh() {
        return R.ok(this.sysRouteConfService.refresh());
    }

    @SysLog("保存或修改路由设置")
    @Operation(summary = "保存或修改路由设置", description = "保存或修改路由设置(sys_route_insert)")
    @PreAuthorize("@pms.hasPermission('sys_route_insert')")
    @PostMapping({"/addOrUpdate"})
    public R<Boolean> addOrUpdate(@RequestBody @Valid RouteDto routeDto) {
        log.info("修改路由信息：{}", JSONUtil.toJsonStr(routeDto));
        SysRouteConf sysRouteConf = new SysRouteConf();
        BeanUtils.copyProperties(routeDto, sysRouteConf);
        return R.ok(this.sysRouteConfService.saveOrUpdate(sysRouteConf));
    }

    @SysLog("删除路由信息")
    @Operation(summary = "删除路由信息", description = "删除路由信息(sys_route_del)")
    @PreAuthorize("@pms.hasPermission('sys_route_del')")
    @DeleteMapping({"delete/{id}"})
    public R<Boolean> remove(@PathVariable("id") Integer id) {
        return R.ok(this.sysRouteConfService.removeById(id));
    }
}
