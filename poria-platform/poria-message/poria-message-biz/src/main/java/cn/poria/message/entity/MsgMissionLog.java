package cn.poria.message.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * (MsgMissionLog)表实体类
 *
 * @author makejava
 * @since 2025-04-09 09:54:03
 */
@SuppressWarnings("serial")
@Data
public class MsgMissionLog extends Model<MsgMissionLog> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    @Schema(description = "发送者")
    private String fromUser;

    @Schema(description = "接收者")
    private String toUser;

    @Schema(description = "阅读状态 0--未阅读 1--已阅读")
    private Integer status;

    @Schema(description = "类型  1--文本 2--健康宣教")
    private Integer type;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "服务流程来源  1--筛查  2--慢病")
    private Integer form;

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

