package cn.poria.base.vo.request.banner;

import cn.poria.base.vo.request.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BannerQueryModel extends Page {

    @Schema(description = "标题长度200")
    private String title;

    @Schema(description = "状态1-启用，2-停用")
    private Integer status;

}

