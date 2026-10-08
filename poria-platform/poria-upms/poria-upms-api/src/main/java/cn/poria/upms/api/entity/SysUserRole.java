package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户角色")
public class SysUserRole extends Model<SysUserRole> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "角色id")
    private Long roleId;
}
