package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单")
public class SysMenu extends Model<SysMenu> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "menu_id", type = IdType.AUTO)
    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @Schema(description = "菜单权限标识")
    private String permission;

    @Schema(description = "菜单父id")
    @NotNull(message = "菜单父ID不能为空")
    private Long parentId;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "前端路由标识路径")
    private String path;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "菜单类型,0:菜单 1:按钮")
    @NotBlank(message = "菜单类型不能为空")
    private String type;

    @Schema(description = "路由缓冲")
    private String keepAlive;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "所属平台")
    private String platform;
}
