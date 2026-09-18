package cn.poria.upms.service.impl;

import cn.poria.upms.api.entity.SysUserRole;
import cn.poria.upms.mapper.SysUserRoleMapper;
import cn.poria.upms.service.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
   public Boolean deleteByUserId(Long userId) {
      return ((SysUserRoleMapper)this.baseMapper).deleteByUserId(userId);
   }
}
