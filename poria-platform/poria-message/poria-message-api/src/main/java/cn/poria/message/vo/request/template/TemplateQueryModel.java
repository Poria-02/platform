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

    @Schema(description = "模板类型 字典message_type")
    private String type;

    @Schema(description = "模版分类")
    private String category;

    @Schema(description = "模版key")
    private String templateKey;

}
