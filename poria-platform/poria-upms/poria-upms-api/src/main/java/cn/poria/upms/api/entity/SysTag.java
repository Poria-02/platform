package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysTag extends Model<SysTag> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    @Schema(description = "标签key(唯一)")
    @NotBlank(message = "标签key不能为空")
    private String tagKey;

    @Schema(description = "标签分类名称")
    @NotBlank(message = "标签分类名称不能为空")
    private String name;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新时间")
    private Date updateTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新人")
    private String updateBy;

    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "是否删除0未删除，1-已删除")
    private Integer isDelete;
}
