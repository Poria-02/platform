package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysRoleMenuService extends IService<SysRoleMenu> {
   Boolean saveRoleMenus(String role, Integer roleId, String menuIds);
}
