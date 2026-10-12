package cn.poria.message.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * (MsgYxxhInfo)表实体类
 *
 * @author wuyifan
 * @since 2024-03-25 16:15:24
 */
@Data
public class MsgYxxhInfo extends Model<MsgYxxhInfo> {

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "患者手机号")
    private String patientMobile;

    @Schema(description = "医护手机号")
    private String staffMobile;

    @Schema(description = "小号绑定关系唯一标识")
    private String bindId;

    @Schema(description = "绑定的小号号码")
    private String middleNumber;

    @Schema(description = "业务id")
    private String businessId;

    @Schema(description = "删除标示 0:未删除 1:已删除")
    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
