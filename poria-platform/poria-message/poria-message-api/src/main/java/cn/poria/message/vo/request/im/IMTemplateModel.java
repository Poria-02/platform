package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "个人IM消息模板")
public class IMTemplateModel {

    @NotBlank(message = "账号id不能为空")
    @Schema(description = "账号id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
