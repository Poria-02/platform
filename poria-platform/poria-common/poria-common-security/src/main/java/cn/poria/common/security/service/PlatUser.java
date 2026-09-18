package cn.poria.common.security.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.io.Serial;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 扩展用户信息
 */
public class PlatUser extends User implements OAuth2AuthenticatedPrincipal {
    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 用户ID
     */
    @Getter
    private String id;

    /**
     * 部门ID
     */
    @Getter
    private Long orgId;

    /**
     * 手机号
     */
    @Getter
    private String phone;

    /**
     * 头像
     */
    @Getter
    private String avatar;

    //----------------------
    /**
     * 网易云信Token
     */
    @Getter
    @Setter
    private String imToken;

    /**
     * 医护ID
     */
    @Getter
    @Setter
    private String staffId;

    /**
     * 医护类型
     */
    @Getter
    @Setter
    private String staffType;


    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String loginType;

    @Setter
    @Getter
    private Set<String> roles;

    /**
     * @param id                    用户ID
     * @param orgId                 机构id
     * @param phone                 手机号
     * @param avatar                头像
     * @param username              username
     * @param name                  姓名
     * @param password              密码
     * @param enabled               是否可用
     * @param accountNonExpired     账号是否过期
     * @param credentialsNonExpired 密码是否过期
     * @param accountNonLocked      账号是否锁定
     * @param authorities           权限列表
     * @param roles                 角色列表
     */
    public PlatUser(
            String id,
            Long orgId,
            String phone,
            String avatar,
            String username,
            String name,
            String password,
            String loginType,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities,
            Set<String> roles
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = id;
        this.orgId = orgId;
        this.phone = phone;
        this.avatar = avatar;
        this.name = name;
        this.loginType = loginType;
        this.roles = roles;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<>();
    }
}
