package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class SysSensitiveLog {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人姓名")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "是否删除0未删除，1-已删除")
    private Integer isDelete;

    @Schema(description = "敏感信息")
    private String sensitiveInfo;

    @Schema(description = "用户类型 1-用户，2-患者 3-医护")
    private Integer userType;

    @Schema(description = "查看人姓名")
    private String createName;
}
