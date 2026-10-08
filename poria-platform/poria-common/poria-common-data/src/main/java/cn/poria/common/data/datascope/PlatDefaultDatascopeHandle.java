package cn.poria.common.data.datascope;

import cn.poria.common.security.service.PlatUser;
import cn.poria.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 根据当前用户的有效角色计算范围；调用方指定的范围只能缩小权限。 */
@Slf4j
@RequiredArgsConstructor
public class PlatDefaultDatascopeHandle implements DataScopeHandle {

    private final DataScopeRoleProvider roleProvider;

    @Override
    public Boolean calcScope(DataScope scope) {
        // 保留原始查询条件，避免分页或重复查询时将空交集误判为无限制。
        scope.setResolvedDeptList(new ArrayList<>());
        scope.setAllDepartments(false);
        PlatUser user = SecurityUtils.getUser();
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }
        if (scope.getUsername() != null && !scope.getUsername().isBlank()
                && !scope.getUsername().equals(user.getUsername())) {
            return false;
        }

        Set<Long> allowed = new LinkedHashSet<>();
        boolean all = false;
        try {
            List<SysRole> roles = roleProvider.getRoles(user.getRoles());
            if (roles == null) {
                return false;
            }
            for (SysRole role : roles) {
                if (role == null || role.getRoleId() == null
                        || !user.getRoles().contains(role.getRoleId().toString())
                        || !"0".equals(role.getDelFlag()) || role.getDsType() == null) {
                    continue;
                }
                switch (role.getDsType()) {
                    case 0 -> all = true;
                    case 1 -> allowed.addAll(parseDepartments(role.getDsScope()));
                    case 3 -> {
                        if (user.getOrgId() != null && user.getOrgId() > 0) {
                            allowed.add(user.getOrgId());
                        }
                    }
                    default -> { /* 已移除的类型 2 及未知类型不授予数据范围。 */ }
                }
            }
        } catch (RuntimeException ex) {
            log.warn("读取角色数据权限失败，拒绝本次数据查询", ex);
            return false;
        }

        List<Long> requested = scope.getDeptList();
        if (requested != null && !requested.isEmpty()) {
            if (requested.stream().anyMatch(id -> id == null || id <= 0)) {
                return false;
            }
            if (all) {
                allowed.clear();
                allowed.addAll(requested);
            } else {
                allowed.retainAll(requested);
            }
            all = false;
        }
        if (Boolean.TRUE.equals(scope.getIsOnly())) {
            Long ownDept = user.getOrgId();
            if (ownDept == null || ownDept <= 0) {
                allowed.clear();
            } else if (all) {
                allowed.clear();
                allowed.add(ownDept);
            } else {
                allowed.retainAll(Set.of(ownDept));
            }
            all = false;
        }
        scope.setResolvedDeptList(new ArrayList<>(allowed));
        scope.setAllDepartments(all);
        return all && (scope.getUsername() == null || scope.getUsername().isBlank());
    }

    private Set<Long> parseDepartments(String value) {
        Set<Long> ids = new LinkedHashSet<>();
        if (value == null || value.isBlank()) {
            return ids;
        }
        try {
            for (String part : value.split(",", -1)) {
                if (!part.trim().matches("[1-9][0-9]*")) {
                    return Set.of();
                }
                ids.add(Long.parseLong(part.trim()));
            }
        } catch (NumberFormatException ex) {
            return Set.of();
        }
        return ids;
    }
}
