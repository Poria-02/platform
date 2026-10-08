package cn.poria.upms.service.impl;

import cn.poria.common.data.datascope.DataScopeRoleProvider;
import cn.poria.common.data.datascope.SysRole;
import cn.poria.upms.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/** 本服务查询角色不再通过 HTTP 回调自己，避免递归与连接池占用。 */
@Component
@RequiredArgsConstructor
public class UpmsDataScopeRoleProvider implements DataScopeRoleProvider {
    private final ObjectProvider<SysRoleService> roleService;

    @Override
    public List<SysRole> getRoles(Set<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return roleService.getObject().listByIds(roleIds).stream().map(source -> {
            SysRole role = new SysRole();
            role.setRoleId(source.getRoleId());
            role.setDsType(source.getDsType());
            role.setDsScope(source.getDsScope());
            role.setDelFlag(source.getDelFlag());
            return role;
        }).toList();
    }
}
