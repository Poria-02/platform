package cn.poria.message.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 代办任务(MsgPendingTask)表实体类
 *
 * @author makejava
 * @since 2020-07-13 20:11:01
 */
@Data
public class MsgPendingTask {

    /**
     *id
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     *次数
     */

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

    /**
     *创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     *创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private String createBy;

    /**
     *删除标示
     */
    @TableLogic(value = "0",delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "删除标示")
    private Integer isDelete;

    /**
     *完成时间
     */

    @Schema(description = "完成时间")
    private Date commitTime;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "0 不跳转 1 h5 2 原生")
    private Integer redirectType;

    @Schema(description = "0-单人任务,1-多人任务")
    private Integer groupType;

    @Schema(description = "优先级 1-5")
    private Integer level;
}
