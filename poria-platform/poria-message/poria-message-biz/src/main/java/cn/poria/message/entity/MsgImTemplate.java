package cn.poria.message.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * IM消息模板(MsgImTemplate)表实体类
 *
 * @author wuning
 * @since 2025-05-07 10:47:07
 */
@Data
public class MsgImTemplate {

    /**
     *主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private String id;

    /**
     *账号id
     */

    @Schema(description = "账号id")
    private String accountId;

    /**
     *im内容
     */

    @Schema(description = "im内容")
    private String content;

    /**
     *创建人id
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人id")
    private String createBy;

    /**
     *修改人id
     */
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改人id")
    private String updateBy;

    /**
     *创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     *修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     *删除标示 0：未删除 1：已删除
     */
    @TableLogic(value = "0",delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "删除标示 0：未删除 1：已删除")
    private Integer isDelete;
}
