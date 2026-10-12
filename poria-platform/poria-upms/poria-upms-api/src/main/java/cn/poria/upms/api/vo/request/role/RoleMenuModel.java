package cn.poria.upms.api.vo.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色菜单授权请求")
public class RoleMenuModel {

    private Integer roleId;

    private String menuIds;
}
