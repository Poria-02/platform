package cn.poria.base.vo.request;

import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BaseAppVersionModel {

    @NotBlank(message = "id不能为空", groups = Update.class)
    @Schema(description = "id")
    private String id;

    /**
     * App名称,取字典(app_name)
     */

    @NotBlank(message = "App名称不能为空")
    @Size(max = 64, message = "App名称不能超过64个字符")
    @Schema(description = "App名称,取字典(app_name)")
    private String appName;

    /**
     * app系统，取字典(app_os)
     */

    @NotBlank(message = "app系统不能为空")
    @Size(max = 32, message = "app系统不能超过32个字符")
    @Schema(description = "app系统，取字典(app_os)")
    private String appOs;

    /**
     * 版本号
     */
    @NotBlank(message = "版本号不能为空")
    @Size(max = 64, message = "版本号不能超过64个字符")
    @Pattern(regexp = "^[vV]?[0-9]+(?:\\.[0-9]+)*$", message = "版本号格式不正确，请输入如1.0.0")
    @Schema(description = "版本号")
//    @Pattern(regexp = "^V[0-9]+[.][0-9]+[.][0-9]+" ,message = "版本号不合法")
    private String version;

    /**
     * 升级类型(取字典app_update_type)
     */

    @NotBlank(message = "升级类型不能为空", groups = {Create.class, Update.class})
    @Pattern(regexp = "[123]", message = "升级类型不正确")
    @Schema(description = "升级类型(取字典app_update_type)")
    private String appUpdateType;

    /**
     * 提示语，长度600
     */

    @NotBlank(message = "提示语不能为空", groups = {Create.class, Update.class})
    @Size(max = 600, message = "提示语不能超过600个字符")
    @Schema(description = "提示语，长度600")
    private String prompt;

    /**
     * 下载地址长度255
     */
    @NotBlank(message = "下载地址不能为空")
    @Size(max = 255, message = "下载地址不能超过255个字符")
    @Schema(description = "下载地址长度255")
    private String url;

}
