package cn.poria.upms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.upms.api.entity.SysRoleMenu;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final CacheManager cacheManager;

    @Transactional(rollbackFor = {Exception.class})
    @CacheEvict(value = {"menu_details"}, allEntries = true)
    public Boolean saveRoleMenus(String role, Integer roleId, String menuIds) {
        this.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, roleId));
        if (StrUtil.isBlank(menuIds)) {
            return Boolean.TRUE;
        } else {
            List<SysRoleMenu> roleMenuList = Arrays.stream(menuIds.split(",")).map((menuId) -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(Integer.valueOf(menuId));
                return roleMenu;
            }).collect(Collectors.toList());
            Objects.requireNonNull(this.cacheManager.getCache("user_details")).clear();
            return this.saveBatch(roleMenuList);
        }
    }
}
