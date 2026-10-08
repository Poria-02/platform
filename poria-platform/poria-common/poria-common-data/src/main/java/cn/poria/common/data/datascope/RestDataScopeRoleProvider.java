package cn.poria.common.data.datascope;

import cn.poria.common.core.util.R;
import cn.poria.common.core.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

/** 透传用户认证，只读取该用户的角色，不使用匿名或内部标记绕过认证。 */
@RequiredArgsConstructor
public class RestDataScopeRoleProvider implements DataScopeRoleProvider {
    private final RestTemplate restTemplate;
    private final String upmsUrl;

    @Override
    public List<SysRole> getRoles(Set<String> roleIds) {
        HttpServletRequest request = WebUtils.getRequest();
        String authorization = request == null ? null : request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalStateException("数据权限查询缺少用户认证");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        R<List<SysRole>> result = restTemplate.exchange(
                upmsUrl.replaceAll("/+$", "") + "/role/data-scope",
                HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<R<List<SysRole>>>() { }).getBody();
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException("角色数据权限查询失败");
        }
        return result.getData();
    }
}
