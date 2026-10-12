package cn.poria.message.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 发送具体详情(MsgDdLog)表实体类
 *
 * @author makejava
 * @since 2020-08-18 11:04:38
 */
@Data
public class MsgDdLog {


    @TableId(type = IdType.ASSIGN_ID)

    private String id;

    /**
     *业务类型，见 BusinessType
     */

    @Schema(description = "业务类型，见 BusinessType")
    private Integer businessType;

    /**
     *接收手机号
     */

    @Schema(description = "接收手机号")
    private String phone;

    /**
     *内容
     */

    @Schema(description = "内容")
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
    private Integer status;

    /**
     *发送结果
     */

    @Schema(description = "发送结果")
    private String result;

    /**
     *创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     *短信平台
     */

    @Schema(description = "短信平台")
    private Integer platform;

    /**
     *模版KEY
     */

    @Schema(description = "模版KEY")
    private String templateKey;

    /**
     *租户ID
     */

    @Schema(description = "租户ID")
    private Integer tenantId;
}
