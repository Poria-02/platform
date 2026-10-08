package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TreeDictModel {

    @Schema(description = "字典ID")
    @NotBlank(message = "字典ID不能为空")
    private String id;

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "备注")
    private String remark = "";
}
