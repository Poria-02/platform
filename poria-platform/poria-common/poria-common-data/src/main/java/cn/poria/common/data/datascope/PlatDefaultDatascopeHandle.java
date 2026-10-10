package cn.poria.common.data.datascope;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.core.util.RetOps;
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

        PlatUser user = SecurityUtils.getUser();
        Set<String> roleIdList = user == null ? Collections.EMPTY_SET : user.getRoles();
        List<Long> deptList = dataScope.getDeptList();

        // 当前用户的角色为空 , 返回false
        if (CollectionUtil.isEmpty(roleIdList)) {
            return false;
        }

        R<List<SysRole>> result = dataScopeService.getRoleList(new ArrayList<>(roleIdList));

        // @formatter:off
        SysRole role = RetOps.of(result).getData().orElseGet(Collections::emptyList).stream().min(Comparator.comparingInt(SysRole::getDsType)).orElse(null);
        //角色有可能已经删除了
        if (role == null){
            return false;
        }

        Integer dsType = role.getDsType();
        // 查询全部
        if (DataScopeTypeEnum.ALL.getType() == dsType) {
            return true;
        }
        // 自定义
        if (DataScopeTypeEnum.CUSTOM.getType() == dsType && StrUtil.isNotBlank(role.getDsScope())) {
            String dsScope = role.getDsScope();
            deptList.addAll(Arrays.stream(dsScope.split(StrUtil.COMMA)).map(Long::parseLong).toList());
        }

        // 只查询本级
        if (DataScopeTypeEnum.OWN_LEVEL.getType() == dsType) {
            if (user != null) {
                deptList.add(user.getOrgId());
            }else {
                return  false;
            }
        }

        // 查询本级及以下部门
        if (DataScopeTypeEnum.DEPT_LEVEL.getType() == dsType) {
            Long deptId = null;
            if (user != null) {deptId = user.getOrgId();}
            if (deptId == null || deptId <= 0) {
                return false;
            }
            R<List<Long>> body = deptService.getDeptIdsWithChildrenInner(deptId, SecurityConstants.FROM_IN);
            if (body != null && body.isSuccess() && body.getData() != null) {
                deptList.addAll(body.getData().stream()
                        .filter(id -> id != null && id > 0).distinct().toList());
            }
        }


        return false;
    }
}
