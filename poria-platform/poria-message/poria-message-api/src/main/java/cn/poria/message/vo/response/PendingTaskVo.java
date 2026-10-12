package cn.poria.message.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class PendingTaskVo {


    /**
     *id
     */
    @Schema(description = "id")
    private String id;

    /**
     *用户ID
     */

    @Schema(description = "用户ID")
    private String userId;

    /**
     *关联用户ID
     */

    @Schema(description = "关联用户ID")
    private String relateUserId;

    /**
     *关联用户姓名
     */

    @Schema(description = "关联用户姓名")
    private String relateUserName;

    /**
     *关联用户头像
     */

    @Schema(description = "关联用户头像")
    private String relateUserHead;

    @Schema(description = "次数")
    private Integer count;

    /**
     *业务ID
     */

    @Schema(description = "业务ID")
    private String businessId;

    /**
     *代办任务类型(字典pending_task_type)
     */

    @Schema(description = "代办任务类型(字典pending_task_type)")
    private Integer type;

    /**
     *任务跳转路径
     */

    @Schema(description = "任务跳转路径")
    private String url;

    /**
     *状态，0-未处理，1-已处理
     */

    @Schema(description = "状态，0-未处理，1-已处理")
    private Integer status;

    /**
     *任务标题
     */

    @Schema(description = "任务标题")
    private String title;

    /**
     *任务时间
     */

    @Schema(description = "任务时间")
    private String taskTime;


    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "0 不跳转 1 h5 2 原生")
    private Integer redirectType;

}
