package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "前端角色展示对象")
public class RoleVO {

    private Integer roleId;

    private String menuIds;
}
