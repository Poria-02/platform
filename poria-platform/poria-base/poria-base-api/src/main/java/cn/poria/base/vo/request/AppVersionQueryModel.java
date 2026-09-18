package cn.poria.base.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AppVersionQueryModel extends Page{



    @Schema(description = "App名称,取字典(app_name)")
    private String appName;


    @Schema(description = "app系统，取字典(app_os)")
    private String appOs;
}

