package cn.poria.upms.api.vo.request.social;

import cn.hutool.core.annotation.PropIgnore;
import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * 第三方账号新增及修改参数
 */
@Data
public class SocialDetailsModel {

    @NotNull(message = "账号ID不能为空", groups = Update.class)
    @Schema(description = "账号ID，修改时必填")
    private Integer id;

    @NotBlank(message = "账号类型不能为空")
    @Schema(description = "账号类型，WX：公众号，MINI：微信小程序，ALI_MA：支付宝小程序")
    private String type;

    @NotBlank(message = "AppId不能为空")
    @Schema(description = "应用AppId")
    private String appId;

    @NotBlank(message = "AppSecret不能为空", groups = Create.class)
    @Schema(description = "应用密钥，修改时留空保留原密钥")
    @ToString.Exclude
    @PropIgnore
    private String appSecret;

    @Schema(description = "账号描述")
    private String remark;

    @Schema(description = "回调地址")
    private String redirectUrl;
}
