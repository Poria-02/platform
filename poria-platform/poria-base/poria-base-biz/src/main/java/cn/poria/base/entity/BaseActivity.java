package cn.poria.base.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * (BaseActivity)表实体类
 *
 * @author makejava
 * @since 2025-11-27 15:33:02
 */
@Data
public class BaseActivity {

    /**
     *id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    @Schema(description = "页面标题")
    private String name;

    @Schema(description = "0：未发布 1：已发布")
    private Integer isStatus;

    @Schema(description = "跳转链接")
    private String jumpLink;

    @Schema(description = "内容")
    private String contentFocused;

    @Schema(description = "是否跳转 0：否 1 ：是")
    private Integer isSkip;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "页面参数")
    private String titleParameter;

    @Schema(description = "跳转按钮文字")
    private String buttonScript;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改人")
    private String updateBy;

    @TableLogic(value = "0",delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "0-未删除，1-已删除")
    private Integer isDelete;

}


