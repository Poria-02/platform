package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserRoleService extends IService<SysUserRole> {
   Boolean deleteByUserId(Long userId);
}
