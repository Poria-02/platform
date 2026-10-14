package cn.poria.message.vo.request.template;

import cn.poria.common.core.dto.PageSearch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TemplateQueryModel extends PageSearch {

    @Schema(description = "关键字")
    private String  keywords;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "模板类型：1 短信、2 站内信、3 微信公众号、4 小程序", allowableValues = {"1", "2", "3", "4"})
    private String type;

    @Schema(description = "模版分类")
    private String category;

    @Schema(description = "模版key")
    private String templateKey;

}
