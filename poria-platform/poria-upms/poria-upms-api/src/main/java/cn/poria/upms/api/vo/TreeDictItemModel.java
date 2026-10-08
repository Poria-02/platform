package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TreeDictItemModel {

    private String id;

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    private String simpleName = "";

    private String remark = "";

    private String value = "";

    private String ext1 = "";

    private String pid = "";

    @Schema(description = "排序,最长6位")
    private @Max(value = 999999L, message = "排序值过大") @Min(value = 0L, message = "排序值太小") Integer sort;

    private List<TreeDictItemModel> childs;

    @Schema(description = "字典ID")
    @NotBlank(message = "字典ID不能为空")
    private String dictId;
}
