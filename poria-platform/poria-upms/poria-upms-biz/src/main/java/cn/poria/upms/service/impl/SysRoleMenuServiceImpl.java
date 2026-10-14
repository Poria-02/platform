package cn.poria.upms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.upms.api.entity.SysRoleMenu;
import cn.poria.upms.api.entity.SysMenu;
import cn.poria.upms.mapper.SysMenuMapper;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.upms.mapper.SysRoleMenuMapper;
import cn.poria.upms.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final CacheManager cacheManager;

    private final SysMenuMapper sysMenuMapper;

    @Transactional(rollbackFor = {Exception.class})
    @CacheEvict(value = {"menu_details"}, allEntries = true)
    public Boolean saveRoleMenus(String role, Integer roleId, String menuIds) {
        List<SysMenu> personalMenus = this.sysMenuMapper.selectList(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getPlatform, "admin")
                .in(SysMenu::getPermission, "sys_user_profile", "sys_user_edit"));
        Set<String> permissions = personalMenus.stream().map(SysMenu::getPermission).collect(Collectors.toSet());
        if (!permissions.containsAll(Set.of("sys_user_profile", "sys_user_edit"))) {
            throw new ServiceException("缺少个人中心默认权限，请先在菜单管理登记查看和修改权限");
        }
        Set<Integer> ids = new LinkedHashSet<>();
        if (StrUtil.isNotBlank(menuIds)) {
            Arrays.stream(menuIds.split(",")).map(String::trim).map(Integer::valueOf).forEach(ids::add);
        }
        personalMenus.stream().map(menu -> Math.toIntExact(menu.getMenuId())).forEach(ids::add);
        // 所有角色保留消息查看入口及上级目录，不自动授予新增、编辑和删除按钮。
        List<SysMenu> pageMenus = this.sysMenuMapper.selectList(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getPlatform, "admin").eq(SysMenu::getType, "0"));
        Map<Long, SysMenu> pagesById = new HashMap<>();
        pageMenus.forEach(menu -> pagesById.put(menu.getMenuId(), menu));
        Set<String> messagePaths = Set.of("/message/station-letter/index", "/message/announcement/index");
        for (SysMenu page : pageMenus) {
            if (StrUtil.isBlank(page.getPath()) || !messagePaths.contains(page.getPath())) {
                continue;
            }
            Set<Long> visited = new LinkedHashSet<>();
            SysMenu menu = page;
            while (menu != null && visited.add(menu.getMenuId())) {
                ids.add(Math.toIntExact(menu.getMenuId()));
                menu = pagesById.get(menu.getParentId());
            }
        }
        List<SysRoleMenu> roleMenuList = ids.stream().map(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            return roleMenu;
        }).collect(Collectors.toList());
        this.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, roleId));
        if (!this.saveBatch(roleMenuList)) {
            throw new ServiceException("角色权限保存失败");
        }
        Objects.requireNonNull(this.cacheManager.getCache("user_details")).clear();
        return Boolean.TRUE;
    }
}
