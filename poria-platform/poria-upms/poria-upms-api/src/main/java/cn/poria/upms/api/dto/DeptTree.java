package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门树")
public class DeptTree extends TreeNode {

    @Schema(description = "部门名称")
    private String name;
}
