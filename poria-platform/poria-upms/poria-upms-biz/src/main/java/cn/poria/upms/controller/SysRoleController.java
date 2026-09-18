package cn.poria.upms.controller;

import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysRole;
import cn.poria.upms.api.vo.RoleVO;
import cn.poria.upms.service.SysRoleMenuService;
import cn.poria.upms.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/role"})
@Tag(name = "role", description = "角色管理模块")
public class SysRoleController {
    private final SysRoleService sysRoleService;
    private final SysRoleMenuService sysRoleMenuService;

    @GetMapping({"/{id}"})
    // @PreAuthorize("@pms.hasPermission('sys_role_get')")
    @Operation(summary = "通过ID查询角色信息", description = "通过ID查询角色信息 (sys_role_get)")
    public R getById(@PathVariable Integer id) {
        return R.ok( this.sysRoleService.getById(id));
    }

    @Inner
    @GetMapping({"/code/{code}"})
    public R getById(@PathVariable String code) {
        return R.ok((SysRole) this.sysRoleService.getOne( (new LambdaQueryWrapper<SysRole>()).eq(SysRole::getRoleCode, code)));
    }

    @SysLog("添加角色")
    @PostMapping
    // @PreAuthorize("@pms.hasPermission('sys_role_add')")
    @Operation(summary = "添加角色", description = "添加角色 (sys_role_add)")
    public R save(@RequestBody @Valid SysRole sysRole) {
        if (this.sysRoleService.existSysRole(sysRole)) {
            throw new ServiceException("已存在相同名称、相同标识的角色");
        } else {
            return R.ok(this.sysRoleService.save(sysRole));
        }
    }

    @SysLog("修改角色")
    @PutMapping
    // @PreAuthorize("@pms.hasPermission('sys_role_edit')")
    @Operation(summary = "修改角色", description = "修改角色 (sys_role_edit)")
    public R update(@RequestBody @Valid SysRole sysRole) {
        if (this.sysRoleService.existSysRole(sysRole)) {
            throw new ServiceException("已存在相同名称、相同标识的角色");
        } else {
            return R.ok(this.sysRoleService.updateById(sysRole));
        }
    }

    @SysLog("删除角色")
    @DeleteMapping({"/{id}"})
    // @PreAuthorize("@pms.hasPermission('sys_role_del')")
    @Operation(summary = "删除角色", description = "删除角色 (sys_role_del)")
    public R removeById(@PathVariable Integer id) {
        return R.ok(this.sysRoleService.removeRoleById(id));
    }

    @GetMapping({"/list"})
    // @PreAuthorize("@pms.hasPermission('sys_role_list')")
    @Operation(summary = "获取角色列表", description = "获取角色列表 (sys_role_list)")
    public R listRoles() {
        return R.ok(this.sysRoleService.list(Wrappers.emptyWrapper()));
    }

    @GetMapping({"/page"})
    // @PreAuthorize("@pms.hasPermission('sys_role_page')")
    @Operation(summary = "分页查询角色信息", description = "分页查询角色信息 (sys_role_page)")
    public R getRolePage(Page page) {
        return R.ok((Page) this.sysRoleService.page(page, Wrappers.emptyWrapper()));
    }

    @SysLog("更新角色菜单")
    @PutMapping({"/menu"})
    // @PreAuthorize("@pms.hasPermission('sys_role_perm')")
    @Operation(summary = "更新角色菜单", description = "更新角色菜单 (sys_role_perm)")
    public R saveRoleMenus(@RequestBody RoleVO roleVo) {
        SysRole sysRole = (SysRole) this.sysRoleService.getById(roleVo.getRoleId());
        return R.ok(this.sysRoleMenuService.saveRoleMenus(sysRole.getRoleCode(), roleVo.getRoleId(), roleVo.getMenuIds()));
    }

    @PostMapping({"/getRoleList"})
    @Operation(summary = "通过角色ID 查询角色列表", description = "通过角色ID 查询角色列表 (sys_role_getList)")
    @Inner(value = false)
    public R getRoleList(@RequestBody List<String> roleIdList) {
        return R.ok(this.sysRoleService.listByIds(roleIdList));
    }

    public SysRoleController(final SysRoleService sysRoleService, final SysRoleMenuService sysRoleMenuService) {
        this.sysRoleService = sysRoleService;
        this.sysRoleMenuService = sysRoleMenuService;
    }

}
