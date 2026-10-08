package cn.poria.upms.api.dto;

import cn.poria.upms.api.vo.MenuVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单树")
public class MenuTree extends TreeNode implements Serializable {

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单名称")
    private String name;

    private boolean spread = false;

    @Schema(description = "前端路由标识路径")
    private String path;

    @Schema(description = "路由缓冲")
    private String keepAlive;

    @Schema(description = "权限编码")
    private String permission;

    @Schema(description = "菜单类型,0:菜单 1:按钮")
    private String type;

    @Schema(description = "菜单标签")
    private String label;

    @Schema(description = "排序值")
    private Integer sort;

    private Boolean hasChildren;

    @Schema(description = "所属平台")
    private String platform;

    public MenuTree() {
    }

    public MenuTree(long id, String name, long parentId) {
        this.id = id;
        this.name = name;
        this.label = name;
        this.parentId = parentId;
    }

    public MenuTree(long id, String name, MenuTree parent) {
        this.id = id;
        this.name = name;
        this.label = name;
        this.parentId = parent.getId();
    }

    public MenuTree(MenuVO menuVo) {
        this.id = menuVo.getMenuId();
        this.parentId = menuVo.getParentId();
        this.icon = menuVo.getIcon();
        this.name = menuVo.getName();
        this.path = menuVo.getPath();
        this.type = menuVo.getType();
        this.permission = menuVo.getPermission();
        this.label = menuVo.getName();
        this.sort = menuVo.getSort();
        this.keepAlive = menuVo.getKeepAlive();
        this.platform = menuVo.getPlatform();
    }
}
