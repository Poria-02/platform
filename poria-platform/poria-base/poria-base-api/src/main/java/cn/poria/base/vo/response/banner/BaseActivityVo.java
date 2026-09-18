package cn.poria.base.vo.response.banner;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class BaseActivityVo {


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


    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;






}

