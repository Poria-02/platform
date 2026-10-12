package cn.poria.message.vo.response.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "IM模板消息")
public class IMTemplateVo {

    @Schema(description = "id")
    private String id;

    @Schema(description = "账号id")
    private String accountId;

    @Schema(description = "内容")
    private String content;
}
