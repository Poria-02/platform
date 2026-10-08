package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TreeDictVO {

    @Schema(description = "字典ID")
    private String id;

    @Schema(description = "字典Code")
    @NotBlank(message = "字典Code不能为空")
    private String code;

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "备注")
    private String remark = "";

    @Schema(description = "类型 0：系统字典 1：业务字典")
    private int type;

    @Schema(description = "字典内容类型 0：列表 1：树")
    @NotNull(message = "字典内容类型不能为空")
    private Boolean isTree;

    @Schema(description = "类型名称")
    private String typeName;
}
