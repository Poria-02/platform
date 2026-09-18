package cn.poria.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * (BaseAppVersion)表实体类
 *
 * @author makejava
 * @since 2020-08-31 22:14:54
 */
@Data
public class BaseAppVersion {

    /**
     *id
     */     
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    /**
     *App名称,取字典(app_name)
     */     
   
    @Schema(description = "App名称,取字典(app_name)")
    private String appName;

    /**
     *app系统，取字典(app_os)
     */     
   
    @Schema(description = "app系统，取字典(app_os)")
    private String appOs;

    /**
     *版本号
     */     
   
    @Schema(description = "版本号")
    private String version;

    /**
     *升级类型(取字典app_update_type)
     */     
   
    @Schema(description = "升级类型(取字典app_update_type)")
    private String appUpdateType;

    /**
     *提示语，长度600
     */     
   
    @Schema(description = "提示语，长度600")
    private String prompt;

    /**
     *下载地址长度255
     */     
   
    @Schema(description = "下载地址长度255")
    private String url;

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
