package cn.poria.upms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.CommonConstants;
import cn.poria.common.core.constant.enums.MenuTypeEnum;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.dto.MenuTree;
import cn.poria.upms.api.entity.SysMenu;
import cn.poria.upms.api.entity.SysRoleMenu;
import cn.poria.upms.api.vo.MenuVO;
import cn.poria.upms.api.vo.TreeUtil;
import cn.poria.upms.mapper.SysMenuMapper;
import cn.poria.upms.mapper.SysRoleMenuMapper;
import cn.poria.upms.service.SysMenuService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
   private final SysRoleMenuMapper sysRoleMenuMapper;

   @Cacheable(
      value = {"menu_details"},
      key = "#roleId  + '_' + #platform  + '_menu'",
      unless = "#result.isEmpty()"
   )
   public List<MenuVO> findMenuByRoleId(Long roleId, String platform) {
      return ((SysMenuMapper)this.baseMapper).listMenusByRoleId(roleId, platform);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @CacheEvict(
      value = {"menu_details"},
      allEntries = true
   )
   public R removeMenuById(Long id) {
      List<SysMenu> menus = this.list();
      Map<Long, List<Long>> childrenByParentId = new HashMap<>();
      Set<Long> menuIds = new HashSet<>();
      ArrayDeque<Long> pendingIds = new ArrayDeque<>();
      pendingIds.add(id);

      for (SysMenu menu : menus) {
         childrenByParentId.computeIfAbsent(menu.getParentId(), key -> new java.util.ArrayList<>()).add(menu.getMenuId());
      }

      while (!pendingIds.isEmpty()) {
         Long menuId = pendingIds.removeFirst();
         if (!menuIds.add(menuId)) {
            continue;
         }
         List<Long> children = childrenByParentId.get(menuId);
         if (children != null) {
            pendingIds.addAll(children);
         }
      }

      this.sysRoleMenuMapper.delete((Wrapper)Wrappers.<SysRoleMenu>lambdaQuery().in(SysRoleMenu::getMenuId, menuIds));
      return R.ok(this.removeByIds(menuIds));
   }

   @CacheEvict(
      value = {"menu_details"},
      allEntries = true
   )
   public Boolean updateMenuById(SysMenu sysMenu) {
      return this.updateById(sysMenu);
   }

   public List<MenuTree> treeMenu(boolean lazy, Long parentId, String type) {
      if (!lazy) {
         return TreeUtil.buildTree(((SysMenuMapper)this.baseMapper).selectList((Wrapper)(Wrappers.<SysMenu>lambdaQuery().eq(StrUtil.isNotBlank(type), SysMenu::getType, type)).orderByAsc(SysMenu::getSort)), CommonConstants.MENU_TREE_ROOT_ID);
      } else {
         Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;
         return TreeUtil.buildTree(((SysMenuMapper)this.baseMapper).selectList((Wrapper)((Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getParentId, parent)).eq(StrUtil.isNotBlank(type), SysMenu::getType, type)).orderByAsc(SysMenu::getSort)), parent);
      }
   }

   public List<MenuTree> filterMenu(Set<MenuVO> all, String type, Long parentId) {
      List<MenuTree> menuTreeList = (List)all.stream().filter(this.menuTypePredicate(type)).map(MenuTree::new).sorted(Comparator.comparingInt(MenuTree::getSort)).collect(Collectors.toList());
      Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;
      return TreeUtil.build(menuTreeList, parent);
   }

   private Predicate<MenuVO> menuTypePredicate(String type) {
      return (vo) -> {
         if (MenuTypeEnum.TOP_MENU.getDescription().equals(type)) {
            return MenuTypeEnum.TOP_MENU.getType().equals(vo.getType());
         } else {
            return !MenuTypeEnum.BUTTON.getType().equals(vo.getType());
         }
      };
   }

   public SysMenuServiceImpl(final SysRoleMenuMapper sysRoleMenuMapper) {
      this.sysRoleMenuMapper = sysRoleMenuMapper;
   }

}
