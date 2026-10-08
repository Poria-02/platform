package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门")
public class SysDept extends Model<SysDept> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "dept_id", type = IdType.AUTO)
    @Schema(description = "部门id")
    private Long deptId;

    @Schema(description = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    private String name;

    @Schema(description = "排序值")
    @NotNull(message = "排序值不能为空")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "父级部门id")
    private Long parentId;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "部门类型  1-机构，2-机构下属科室")
    private Integer type;

    @Schema(description = "详情ID")
    private String detailId;
}
