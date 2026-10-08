package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysTagItem extends Model<SysTagItem> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    @Schema(description = "标签分类id")
    @NotBlank(message = "标签分类id不能为空")
    private String tagId;

    @Schema(description = "标签名称")
    @NotBlank(message = "标签名称不能为空")
    private String name;

    @Schema(description = "标签值")
    @NotBlank(message = "标签值不能为空")
    private String value;

    @Schema(description = "标签icon地址")
    private String icon;

    @Schema(description = "标签颜色")
    private String color;

    @Schema(description = "状态1-启用，2-停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

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
