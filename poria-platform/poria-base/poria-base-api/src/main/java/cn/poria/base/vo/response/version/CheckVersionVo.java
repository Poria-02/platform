package cn.poria.base.vo.response.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CheckVersionVo {

    @Schema(description = "升级类型(取字典app_update_type)")
    private String appUpdateType;

    @Schema(description = "下载地址长度255")
    private String url;

    @Schema(description = "提示语列表")
    private List<Map<String,String>> prompts;

    @Schema(description = "最新版本号")
    private String version;

}

