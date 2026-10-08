package cn.poria.upms.api.dto;

import cn.poria.upms.api.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统用户传输对象")
public class UserDTO extends SysUser {

    @Schema(description = "角色id集合")
    private List<Long> role;

    @Schema(description = "部门id")
    private Long deptId;

    @Schema(description = "新密码")
    private String newpassword1;
}
