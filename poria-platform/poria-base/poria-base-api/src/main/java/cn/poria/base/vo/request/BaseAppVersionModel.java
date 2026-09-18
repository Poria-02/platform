package cn.poria.base.vo.request;

import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BaseAppVersionModel {


    @NotBlank(message = "id不能为空",groups = Update.class)
    @Schema(description = "id")
    private String id;

    /**
     *App名称,取字典(app_name)
     */

    @NotBlank(message = "App名称不能为空")
    @Schema(description = "App名称,取字典(app_name)")
    private String appName;

    /**
     *app系统，取字典(app_os)
     */

    @NotBlank(message = "app系统不能为空")
    @Schema(description = "app系统，取字典(app_os)")
    private String appOs;

    /**
     *版本号
     */
    @NotBlank(message = "版本号不能为空")
    @Schema(description = "版本号")
//    @Pattern(regexp = "^V[0-9]+[.][0-9]+[.][0-9]+" ,message = "版本号不合法")
    private String version;

    /**
     *升级类型(取字典app_update_type)
     */

    @NotBlank(message = "升级类型不能为空",groups = Create.class)
    @Schema(description = "升级类型(取字典app_update_type)")
    private String appUpdateType;

    /**
     *提示语，长度600
     */

    @NotBlank(message = "提示语不能为空",groups = Create.class)
    @Schema(description = "提示语，长度600")
    private String prompt;

    /**
     *下载地址长度255
     */
    @Schema(description = "下载地址长度255")
    private String url;


}

