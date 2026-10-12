package cn.poria.message.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 短信发送具体详情(MsgEmailLog)表实体类
 *
 * @author makejava
 * @since 2020-07-13 15:10:24
 */
@Data
public class MsgEmailLog {


    @TableId(type = IdType.ASSIGN_ID)

    private String id;

    /**
     *业务类型，见 BusinessType
     */

    @Schema(description = "业务类型，见 BusinessType")
    private Integer businessType;

    /**
     *接收者ID
     */

    @Schema(description = "接收者ID")
    private Integer receiveUserId;

    /**
     *接收手机号
     */

    @Schema(description = "接收手机号")
    private String receiveEmail;




    private String title;




    private String content;

    /**
     *任务ID
     */

    @Schema(description = "任务ID")
    private Integer taskId;

    /**
     *发送状态，1：发送成功，2：失败，0：未发送，3：正在发送
     */

    @Schema(description = "发送状态，1：发送成功，2：失败，0：未发送，3：正在发送")
    private Integer sendStatus;

    /**
     *发送结果
     */

    @Schema(description = "发送结果")
    private String sendResult;

    /**
     *发送时间
     */

    @Schema(description = "发送时间")
    private Date sendTime;


    @TableField(fill = FieldFill.INSERT)

    private Date createTime;
}
