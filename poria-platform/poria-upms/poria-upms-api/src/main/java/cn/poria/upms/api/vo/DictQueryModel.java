package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DictQueryModel extends PageSearch {

    @Schema(description = "字典名称")
    private String name;

    @Schema(description = "字典编号")
    private String code;

    @Schema(description = "字典类型 0-系统字典，1-用户字典")
    private String type;
}
