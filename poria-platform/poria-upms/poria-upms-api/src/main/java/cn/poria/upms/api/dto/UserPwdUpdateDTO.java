package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPwdUpdateDTO {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String password;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newpassword1;
}
