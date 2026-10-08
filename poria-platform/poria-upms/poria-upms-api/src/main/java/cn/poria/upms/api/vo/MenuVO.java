package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Schema(description = "菜单展示对象")
public class MenuVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "菜单权限标识")
    private String permission;

    @Schema(description = "父菜单id")
    private Long parentId;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "前端路由标识路径")
    private String path;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "菜单类型,0:菜单 1:按钮")
    private String type;

    @Schema(description = "路由缓冲")
    private String keepAlive;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "所属平台")
    private String platform;

    public int hashCode() {
        return this.menuId.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof MenuVO) {
            Long targetMenuId = ((MenuVO)obj).getMenuId();
            return this.menuId.equals(targetMenuId);
        } else {
            return super.equals(obj);
        }
    }
}
