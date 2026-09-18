package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {
   List<SysRole> findRolesByUserId(Long userId);

   Boolean removeRoleById(Integer id);

   Boolean existSysRole(SysRole sysRole);
}
