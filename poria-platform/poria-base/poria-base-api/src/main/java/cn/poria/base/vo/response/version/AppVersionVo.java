package cn.poria.base.vo.response.version;

import cn.poria.common.core.validate.Create;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class AppVersionVo {

    @Schema(description = "id")
    private String id;

    /**
     *App名称,取字典(app_name)
     */

    @NotBlank(message = "App名称不能为空",groups = Create.class)
    @Schema(description = "App名称,取字典(app_name)")
    private String appName;

    /**
     *app系统，取字典(app_os)
     */

    @NotBlank(message = "app系统不能为空",groups = Create.class)
    @Schema(description = "app系统，取字典(app_os)")
    private String appOs;

    /**
     *版本号
     */
    @NotBlank(message = "版本号不能为空",groups = Create.class)
    @Schema(description = "版本号")
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
    @NotBlank(message = "下载地址长度不能为空",groups = Create.class)
    @Schema(description = "下载地址长度255")
    private String url;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "是否是最新版本")
    private Boolean isNew;

}

