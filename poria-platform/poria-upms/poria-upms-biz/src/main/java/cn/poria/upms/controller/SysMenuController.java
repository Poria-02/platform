package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysMenu;
import cn.poria.upms.api.vo.MenuVO;
import cn.poria.upms.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/menu"})
@Tag(name = "menu", description = "菜单管理模块")
public class SysMenuController {
   private final SysMenuService sysMenuService;

   @GetMapping
   @Operation(summary = "返回当前用户的树形菜单集合", description = "返回当前用户的树形菜单集合 (sys_menu_get)")
   public R getUserMenu(String type, Long parentId, String platform) {
      Set<MenuVO> all = new HashSet();
      SecurityUtils.getRoles().forEach((roleId) -> all.addAll(this.sysMenuService.findMenuByRoleId(roleId, platform)));
      return R.ok(this.sysMenuService.filterMenu(all, type, parentId));
   }

   @GetMapping({"/tree"})
   @Operation(summary = "返回树形菜单集合", description = "返回树形菜单集合")
   public R getTree(boolean lazy, Long parentId, String type) {
      return R.ok(this.sysMenuService.treeMenu(lazy, parentId, type));
   }

   @GetMapping({"/tree/{roleId}"})
   @Operation(summary = "返回角色的菜单集合", description = "返回角色的菜单集合")
   public R getRoleTree(@PathVariable Long roleId) {
      return R.ok((List)this.sysMenuService.findMenuByRoleId(roleId, (String)null).stream().map(MenuVO::getMenuId).collect(Collectors.toList()));
   }

   @GetMapping({"/{id}"})
   @Operation(summary = "通过ID查询菜单的详细信息", description = "通过ID查询菜单的详细信息 (sys_menu_detail)")
   public R getById(@PathVariable Integer id) {
      return R.ok((SysMenu)this.sysMenuService.getById(id));
   }

   @SysLog("新增菜单")
   @PostMapping
   @Operation(summary = "新增菜单", description = "新增菜单 (sys_menu_add)")
   public R save(@RequestBody @Valid SysMenu sysMenu) {
      this.sysMenuService.save(sysMenu);
      return R.ok(sysMenu);
   }

   @SysLog("删除菜单")
   @DeleteMapping({"/{id}"})
   @Operation(summary = "删除菜单", description = "删除菜单 (sys_menu_del)")
   public R removeById(@PathVariable Long id) {
      return this.sysMenuService.removeMenuById(id);
   }

   @SysLog("更新菜单")
   @PutMapping
   @Operation(summary = "更新菜单", description = "更新菜单 (sys_menu_edit)")
   public R update(@RequestBody @Valid SysMenu sysMenu) {
      return R.ok(this.sysMenuService.updateMenuById(sysMenu));
   }

   public SysMenuController(final SysMenuService sysMenuService) {
      this.sysMenuService = sysMenuService;
   }
}
