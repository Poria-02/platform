package cn.poria.message.vo.request.ma;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class MaTemplateQueryModel {

    @Schema(description = "小程序id")
    private String appid;

    @Schema(description = "模版key列表")
    private List<String> templateKey;

}
