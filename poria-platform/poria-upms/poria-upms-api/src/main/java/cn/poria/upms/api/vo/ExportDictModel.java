package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ExportDictModel {

    @Schema(description = "字典类型")
    private List<String> dictTypes;

    @Schema(description = "分级菜单code")
    private List<String> dictTreeCode;
}
