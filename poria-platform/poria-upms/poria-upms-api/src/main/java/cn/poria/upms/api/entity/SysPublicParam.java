package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公共参数")
public class SysPublicParam extends Model<SysPublicParam> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "公共参数编号")
    private String publicId;

    @Schema(description = "公共参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "公共参数名称")
    @NotBlank(message = "公共参数名称不能为空")
    private String publicName;

    @Schema(description = "键[英文大写+下划线]", requiredMode = Schema.RequiredMode.REQUIRED, example = "PIGX_PUBLIC_KEY")
    @NotBlank(message = "键不能为空")
    private String publicKey;

    @Schema(description = "值", requiredMode = Schema.RequiredMode.REQUIRED, example = "999")
    @NotBlank(message = "值不能为空")
    private String publicValue;

    @Schema(description = "标识[1有效；2无效]", example = "1")
    private String status;

    @TableLogic
    @Schema(description = "状态[0-正常，1-删除]", example = "0")
    private String delFlag;

    @Schema(description = "编码", example = "^(PIG|PIGX)$")
    private String validateCode;

    @Schema(description = "创建时间", example = "2019-03-21 12:28:48")
    private Date createTime;

    @Schema(description = "修改时间", example = "2019-03-21 12:28:48")
    private Date updateTime;

    @TableField("`system`")
    @Schema(description = "是否是系统内置")
    private String system;

    @Schema(description = "类型[1-检索；2-原文...]", example = "1")
    private String publicType;
}
