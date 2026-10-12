package cn.poria.message.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 云信小号通话记录(MsgYxxhLog)表实体类
 *
 * @author wuyifan
 * @since 2024-03-26 10:23:00
 */
@Data
public class MsgYxxhLog extends Model<MsgYxxhLog> {

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "用户账号")
    private String appId;

    @Schema(description = "小号绑定关系唯一标识")
    private String bindId;

    @Schema(description = "单个呼叫时返回，用于标识每通电话")
    private String callId;

    @Schema(description = "主显号码--小号")
    private String displayNumber;

    @Schema(description = "主叫号码")
    private String callerNumber;

    @Schema(description = "被叫号码")
    private String calleeNumber;

    @Schema(description = "呼叫开始时间")
    private String startCallTime;

    @Schema(description = "主叫接通时间")
    private String callerAnsweredTime;

    @Schema(description = "被叫接通时间")
    private String calleeAnsweredTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "主叫呼叫状态")
    private Integer callerState;

    @Schema(description = "被叫呼叫状态")
    private Integer calleeState;

    @Schema(description = "主叫通话时长--单位秒")
    private Integer callerDuration;

    @Schema(description = "被叫通话时长--单位秒")
    private Integer calleeDuration;

    @Schema(description = "时长（计费）--单位秒")
    private Integer duration;

    @Schema(description = "录音文件下载")
    private String recUrl;

    @Schema(description = "用户自定义数据--业务id")
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
