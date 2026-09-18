package cn.poria.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * banner主表(BaseBanner)表实体类
 *
 * @author makejava
 * @since 2020-09-02 22:47:51
 */
@Data
public class BaseBanner {

    /**
     *主键
     */     
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

    /**
     *标题长度200
     */     
   
    @Schema(description = "标题长度200")
    private String title;

    /**
     *code 长度20
     */     
   
    @Schema(description = "code 长度20")
    private String code;

    /**
     *描述 长度600
     */     
   
    @Schema(description = "描述 长度600")
    private String description;

    /**
     *状态1-启用，2-停用
     */     
   
    @Schema(description = "状态1-启用，2-停用")
    private Integer status;

    /**
     *创建时间
     */     
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     *创建者
     */     
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;

    /**
     *修改时间
     */     
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     *修改人
     */     
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改人")
    private String updateBy;

    /**
     *租户ID
     */     
   
    @Schema(description = "租户ID")
    private Integer tenantId;

    /**
     *0-未删除，1-已删除
     */     
    @TableLogic(value = "0",delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "0-未删除，1-已删除")
    private Integer isDelete;
}
