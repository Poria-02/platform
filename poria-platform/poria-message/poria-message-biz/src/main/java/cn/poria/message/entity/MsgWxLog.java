package cn.poria.message.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 短信发送具体详情(MsgWxLog)表实体类
 *
 * @author makejava
 * @since 2020-08-20 22:12:17
 */
@Data
public class MsgWxLog {


    @TableId(type = IdType.ASSIGN_ID)

    private String id;

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
     *参数
     */

    @Schema(description = "参数")
    private String params;

    /**
     *创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     *模版KEY
     */

    @Schema(description = "模版KEY")
    private String templateKey;

    /**
     *微信返回消息id
     */

    @Schema(description = "微信返回消息id")
    private String msgId;

    /**
     *接收消息用户账户
     */

    @Schema(description = "接收消息用户账户")
    private String accountId;

    /**
     *机构id
     */

    @Schema(description = "机构id")
    private Long orgId;
}
