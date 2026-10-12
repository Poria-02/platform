package cn.poria.message.vo.request.im;

import cn.poria.common.core.dto.PageSearch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(title = "im模板消息查询")
public class IMTemplateQueryModel extends PageSearch {

    @Schema(description = "账号id")
    private String accountId;

    @Schema(description = "内容")
    private String content;
}
