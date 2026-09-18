package cn.poria.base.vo.response.banner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class BannerVo {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "标题长度200")
    private String title;

    @Schema(description = "code 长度20")
    private String code;

    @Schema(description = "描述 长度600")
    private String description;

    @Schema(description = "状态1-启用，2-停用")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createTime;

}

