package cn.poria.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 公示公告(MsgAnnouncement)表实体类
 */
@Data
@TableName("msg_announcement")
public class MsgAnnouncement {

    /**
     * 公告ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告正文
     */
    private String content;

    /**
     * 发布状态，0-草稿，1-已发布，2-已撤回
     */
    private Integer status;

    /**
     * 发布人名称
     */
    private String publisher;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;

}
