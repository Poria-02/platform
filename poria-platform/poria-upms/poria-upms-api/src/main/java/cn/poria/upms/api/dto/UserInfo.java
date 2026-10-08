package cn.poria.upms.api.dto;

import cn.poria.upms.api.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户信息")
public class UserInfo implements Serializable {

    @Schema(description = "用户基本信息")
    private SysUser sysUser;

    @Schema(description = "权限标识集合")
    private String[] permissions;

    @Schema(description = "角色标识集合")
    private Long[] roles;
}
