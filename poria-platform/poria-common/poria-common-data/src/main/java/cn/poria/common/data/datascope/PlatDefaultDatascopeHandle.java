package cn.poria.common.data.datascope;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.security.service.PlatUser;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysRole;
import cn.poria.upms.api.feign.RemoteDataScopeService;
import cn.poria.upms.api.feign.RemoteDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 根据当前用户的有效角色计算范围；调用方指定的范围只能缩小权限。
 */
@Slf4j
@RequiredArgsConstructor
public class PlatDefaultDatascopeHandle implements DataScopeHandle {

    private final RemoteDataScopeService dataScopeService;

    private final RemoteDeptService deptService;

    @Override
    public Boolean calcScope(DataScope dataScope) {

        dataScope.setResolvedDeptList(new ArrayList<>());
        dataScope.setAllDepartments(false);
        PlatUser user = SecurityUtils.getUser();
        Set<String> roleIdList = user == null ? Collections.emptySet() : user.getRoles();
        if (CollectionUtil.isEmpty(roleIdList)) {
            return false;
        }
        R<List<SysRole>> result = dataScopeService.getRoleList(new ArrayList<>(roleIdList));
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return false;
        }
        // 保留原有角色类型优先级，只使用当前账号实际拥有的有效角色。
        SysRole role = result.getData().stream()
                .filter(Objects::nonNull)
                .filter(item -> roleIdList.contains(String.valueOf(item.getRoleId())))
                .filter(item -> item.getDsType() != null && item.getDsType() >= 0 && item.getDsType() <= 3)
                .min(Comparator.comparingInt(SysRole::getDsType)).orElse(null);
        if (role == null) {
            return false;
        }
        Integer dsType = role.getDsType();
        boolean allDepartments = DataScopeTypeEnum.ALL.getType() == dsType;
        Set<Long> allowedDepartments = new LinkedHashSet<>();
        if (DataScopeTypeEnum.CUSTOM.getType() == dsType && StrUtil.isNotBlank(role.getDsScope())) {
            try {
                for (String value : role.getDsScope().split(StrUtil.COMMA, -1)) {
                    long id = Long.parseLong(value.trim());
                    if (id <= 0) {
                        return false;
                    }
                    allowedDepartments.add(id);
                }
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        if (DataScopeTypeEnum.OWN_LEVEL.getType() == dsType && user.getOrgId() != null && user.getOrgId() > 0) {
            allowedDepartments.add(user.getOrgId());
        }
        if (DataScopeTypeEnum.DEPT_LEVEL.getType() == dsType) {
            Long deptId = user.getOrgId();
            if (deptId == null || deptId <= 0) {
                return false;
            }
            R<List<Long>> body = deptService.getDeptIdsWithChildrenInner(deptId, SecurityConstants.FROM_IN);
            if (body == null || !body.isSuccess() || body.getData() == null) {
                return false;
            }
            body.getData().stream().filter(id -> id != null && id > 0).forEach(allowedDepartments::add);
        }
        // 查询部门是筛选条件，只能与账号允许的范围取交集。
        if (CollectionUtil.isNotEmpty(dataScope.getDeptList())) {
            Set<Long> requestedDepartments = new LinkedHashSet<>();
            dataScope.getDeptList().stream().filter(id -> id != null && id > 0).forEach(requestedDepartments::add);
            if (allDepartments) {
                allowedDepartments.addAll(requestedDepartments);
                allDepartments = false;
            } else {
                allowedDepartments.retainAll(requestedDepartments);
            }
        }
        if (Boolean.TRUE.equals(dataScope.getIsOnly())) {
            Long deptId = user.getOrgId();
            if (allDepartments) {
                if (deptId != null && deptId > 0) {
                    allowedDepartments.add(deptId);
                }
                allDepartments = false;
            } else {
                allowedDepartments.removeIf(id -> !id.equals(deptId));
            }
        }
        dataScope.setResolvedDeptList(new ArrayList<>(allowedDepartments));
        dataScope.setAllDepartments(allDepartments);
        return allDepartments && StrUtil.isBlank(dataScope.getUsername());
    }
}
