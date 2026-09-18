package cn.poria.base.vo.response.banner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class AppBannerItemVo implements Serializable {


    @Schema(description = "内容 5000")
    private String content;

    @Schema(description = "图片地址")
    private String imageUrl;

    @Schema(description = "标题 200")
    private String title;


    @Schema(description = "是否需要登录1-需要，2-不需要")
    private Integer needLogin;

    @Schema(description = "广告项类型(字典)")
    private String bannerItemType;

}

