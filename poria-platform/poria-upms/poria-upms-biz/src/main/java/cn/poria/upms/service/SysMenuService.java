package cn.poria.upms.service;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.dto.MenuTree;
import cn.poria.upms.api.entity.SysMenu;
import cn.poria.upms.api.vo.MenuVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Set;

public interface SysMenuService extends IService<SysMenu> {
   List<MenuVO> findMenuByRoleId(Long roleId, String platform);

   R removeMenuById(Long id);

   Boolean updateMenuById(SysMenu sysMenu);

   List<MenuTree> treeMenu(boolean lazy, Long parentId, String type);

   List<MenuTree> filterMenu(Set<MenuVO> voSet, String type, Long parentId);
}
