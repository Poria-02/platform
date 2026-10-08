package cn.poria.common.data.datascope;

import java.util.List;
import java.util.Set;

/** 数据权限角色来源，UPMS 使用本地数据库，其他服务读取当前登录用户的角色。 */
@FunctionalInterface
public interface DataScopeRoleProvider {
    List<SysRole> getRoles(Set<String> roleIds);
}
