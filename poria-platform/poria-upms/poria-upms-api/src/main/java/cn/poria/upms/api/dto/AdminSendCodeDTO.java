package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminSendCodeDTO {

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "用户类型")
    @NotBlank(message = "类型不能为空")
    private String type;

    @Schema(description = "随机码")
    @NotBlank(message = "随机码不能为空")
    private String randomStr;

    @Schema(description = "图片验证码")
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
