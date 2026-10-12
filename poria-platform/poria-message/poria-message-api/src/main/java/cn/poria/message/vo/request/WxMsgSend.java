package cn.poria.message.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class WxMsgSend {

    @NotBlank(message="key不能为空")
    private String key;

    @Schema(description = "参数列表")
    private Map<String,String> param;

    @Schema(description = "用户id")
    @NotBlank(message = "accountId不能为空")
    private String accountId;

    @Schema(description = "机构id")
    private Long orgId;

    @Schema(description = "企业ID")
    private String companyId;

    @Schema(description = "appId")
    private String appId;

    @Schema(description = "业务来源")
    private String source;
}
