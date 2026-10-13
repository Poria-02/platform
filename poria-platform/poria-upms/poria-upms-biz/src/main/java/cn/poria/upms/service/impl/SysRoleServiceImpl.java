package cn.poria.upms.service.impl;

import cn.poria.upms.api.entity.SysRole;
import cn.poria.upms.api.entity.SysRoleMenu;
import cn.poria.upms.mapper.SysRoleMapper;
import cn.poria.upms.mapper.SysRoleMenuMapper;
import cn.poria.upms.service.SysRoleService;
import cn.poria.upms.service.SysRoleMenuService;
import cn.poria.common.core.exception.ServiceException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    private final SysRoleMenuService sysRoleMenuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SysRole role) {
        if (!super.save(role)) {
            return false;
        }
        if (!Boolean.TRUE.equals(this.sysRoleMenuService.saveRoleMenus(
                role.getRoleCode(), Math.toIntExact(role.getRoleId()), ""))) {
            throw new ServiceException("角色默认个人中心权限保存失败");
        }
        return true;
    }

    public List<SysRole> findRolesByUserId(Long userId) {
        return this.baseMapper.listRolesByUserId(userId);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean removeRoleById(Integer id) {
        this.sysRoleMenuMapper.delete(Wrappers.<SysRoleMenu>lambdaUpdate().eq(SysRoleMenu::getRoleId, id));
        return this.removeById(id);
    }

    public Boolean existSysRole(SysRole sysRole) {
        return this.getBaseMapper().existSysRole(sysRole) > 0;
    }
}
