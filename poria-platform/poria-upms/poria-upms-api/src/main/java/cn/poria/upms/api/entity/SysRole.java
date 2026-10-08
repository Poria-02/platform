package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色")
public class SysRole extends Model<SysRole> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "role_id", type = IdType.AUTO)
    @Schema(description = "角色编号")
    private Long roleId;

    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色标识")
    @NotBlank(message = "角色标识不能为空")
    private String roleCode;

    @Schema(description = "角色描述")
    private String roleDesc;

    @Schema(description = "数据权限类型")
    @NotNull(message = "数据权限类型不能为空")
    private Integer dsType;

    @Schema(description = "数据权限作用范围")
    private String dsScope;

    @JsonIgnore
    @AssertTrue(message = "数据权限仅支持全部、自定义、本级；自定义范围请选择有效部门")
    public boolean isDataScopeValid() {
        if (dsType == null || dsType == 0 || dsType == 3) {
            return true;
        }
        if (dsType != 1 || dsScope == null || dsScope.isBlank()) {
            return false;
        }
        try {
            for (String part : dsScope.split(",", -1)) {
                if (!part.trim().matches("[1-9][0-9]*") || Long.parseLong(part.trim()) <= 0) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;
}
