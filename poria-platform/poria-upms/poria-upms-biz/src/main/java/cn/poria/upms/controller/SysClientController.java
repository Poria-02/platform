package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysOauthClientDetails;
import cn.poria.upms.service.SysOauthClientDetailsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/client"})
@Tag(name = "客户端管理模块", description = "客户端管理模块")
public class SysClientController {

    @Resource
    private final SysOauthClientDetailsService clientDetailsService;

    @GetMapping({"/{clientId}"})
    @PreAuthorize("@pms.hasPermission('sys_client_get')")
    @Operation(summary = "通过ID查询", description = "通过ID查询 (sys_client_get)")
    public R getByClientId(@PathVariable String clientId) {
        return R.ok(this.clientDetailsService.list(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId)));
    }

    @GetMapping({"/page"})
    @PreAuthorize("@pms.hasPermission('sys_client_page')")
    @Operation(summary = "分页查询", description = "分页查询 (sys_client_page)")
    public R getOauthClientDetailsPage(Page page, SysOauthClientDetails sysOauthClientDetails) {
        return R.ok(this.clientDetailsService.page(page, Wrappers.query(sysOauthClientDetails)));
    }

    @SysLog("添加终端")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_client_add')")
    @Operation(summary = "添加终端", description = "添加终端（sys_client_add）")
    public R add(@RequestBody @Valid SysOauthClientDetails sysOauthClientDetails) {
        return R.ok(this.clientDetailsService.save(sysOauthClientDetails));
    }

    @SysLog("删除终端")
    @DeleteMapping({"/{clientId}"})
    @PreAuthorize("@pms.hasPermission('sys_client_delete')")
    @Operation(summary = "删除终端", description = "删除终端（sys_client_delete）")
    public R removeById(@PathVariable String clientId) {
        return R.ok(this.clientDetailsService.removeByClientId(clientId));
    }

    @SysLog("编辑终端")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_client_edit')")
    @Operation(summary = "编辑终端", description = "编辑终端（sys_client_edit）")
    public R update(@RequestBody @Valid SysOauthClientDetails sysOauthClientDetails) {
        return R.ok(this.clientDetailsService.updateClientById(sysOauthClientDetails));
    }

    @SysLog("清除终端缓存")
    @DeleteMapping({"/cache"})
    @PreAuthorize("@pms.hasPermission('sys_client_del')")
    @Operation(summary = "清除终端缓存", description = "清除终端缓存（sys_client_del）")
    public R clearClientCache() {
        this.clientDetailsService.clearClientCache();
        return R.ok();
    }

    @Inner
    @GetMapping({"/getClientDetailsById/{clientId}"})
    public R getClientDetailsById(@PathVariable String clientId) {
        return R.ok(this.clientDetailsService.getOne(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false));
    }
}
