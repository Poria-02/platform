package cn.poria.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 公示公告(MsgAnnouncement)表实体类
 */
@Data
@TableName("msg_announcement")
@Schema(description = "公示公告实体")
public class MsgAnnouncement {

    /**
     * 公告ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "公告ID")
    private String id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题")
    private String title;

    /**
     * 公告正文
     */
    @Schema(description = "公告正文")
    private String content;

    /**
     * 发布状态，0-草稿，1-已发布，2-已撤回
     */
    @Schema(description = "发布状态：0-草稿，1-已发布，2-已撤回", allowableValues = {"0", "1", "2"})
    private Integer status;

    /**
     * 发布人名称
     */
    @Schema(description = "发布人名称")
    private String publisher;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "逻辑删除标识：0-未删除，1-已删除", allowableValues = {"0", "1"})
    private Integer isDelete;

}
