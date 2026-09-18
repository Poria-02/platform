

package cn.poria.common.data.datascope;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.core.util.RetOps;
import cn.poria.common.data.conver.model.ConverReq;
import cn.poria.common.security.service.PlatUser;
import cn.poria.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认data scope 判断处理器
 */
@RequiredArgsConstructor
public class PlatDefaultDatascopeHandle implements DataScopeHandle {

    private final RestTemplate restTemplate;
	/**
	 * 计算用户数据权限
	 * @param dataScope 数据权限范围
	 * @return
	 */
	@Override
	public Boolean calcScope(DataScope dataScope) {
		PlatUser user = SecurityUtils.getUser();
		Set<String> roleIdList = user == null ? Collections.EMPTY_SET : user.getRoles();
		List<Long> deptList = dataScope.getDeptList();

        // 当前用户的角色为空 , 返回false
        if (CollectionUtil.isEmpty(roleIdList)) {
            return false;
        }

////        R<List<SysRole>> result = restTemplate.postForEntity(ServiceNameConstants.UPMS_SERVICE + "/role/getRoleList", roleIdList, R.class).getBody();
//        R<List<SysRole>> result = restTemplate.postForEntity("http://poria-upms:4000" + "/role/getRoleList", roleIdList, R.class).getBody();
//
//		// @formatter:off
//        SysRole role = RetOps.of(result)
//				.getData()
//				.orElseGet(Collections::emptyList).stream()
//				.min(Comparator.comparingInt(SysRole::getDsType)).orElse(null);
//		//角色有可能已经删除了
//		if (role == null){
//			return false;
//		}

		Integer dsType = 0;
		// 查询全部
		if (DataScopeTypeEnum.ALL.getType() == dsType) {
			return true;
		}
//		// 自定义
//		if (DataScopeTypeEnum.CUSTOM.getType() == dsType && StrUtil.isNotBlank(role.getDsScope())) {
//			String dsScope = role.getDsScope();
//			deptList.addAll(
//					Arrays.stream(dsScope.split(StrUtil.COMMA)).map(Long::parseLong).collect(Collectors.toList()));
//		}

        // 只查询本级
        if (DataScopeTypeEnum.OWN_LEVEL.getType() == dsType) {
            deptList.add(user.getOrgId());
        }

		return false;
	}

}
