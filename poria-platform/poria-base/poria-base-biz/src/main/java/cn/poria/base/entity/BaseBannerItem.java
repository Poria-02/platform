package cn.poria.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * banner项(BaseBannerItem)表实体类
 *
 * @author makejava
 * @since 2020-09-03 14:48:58
 */
@Data
public class BaseBannerItem {

    /**
     *id
     */     
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    /**
     *bannerId
     */     
   
    @Schema(description = "bannerId")
    private String bannerId;

    /**
     *广告项类型(字典)
     */     
   
    @Schema(description = "广告项类型(字典)")
    private String bannerItemType;

    /**
     *标题 200
     */     
   
    @Schema(description = "标题 200")
    private String title;

    /**
     *图片地址
     */     
   
    @Schema(description = "图片地址")
    private String imageUrl;

    /**
     *起始投放日期
     */     
   
    @Schema(description = "起始投放日期")
    private Date beginDate;

    /**
     *截止投放日期
     */     
   
    @Schema(description = "截止投放日期")
    private Date endDate;

    /**
     *内容 5000
     */     
   
    @Schema(description = "内容 5000")
    private String content;

    /**
     *排序 2
     */     
   
    @Schema(description = "排序 2")
    private Integer sort;

    /**
     *状态1-启用2-停用
     */     
   
    @Schema(description = "状态1-启用2-停用")
    private Integer status;

    /**
     *是否需要登录1-需要，2-不需要
     */     
   
    @Schema(description = "是否需要登录1-需要，2-不需要")
    private Integer needLogin;

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
