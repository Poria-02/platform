package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典项")
public class SysDictItem extends Model<SysDictItem> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "字典项id")
    private String id;

    @Schema(description = "所属字典类id")
    @NotBlank(message = "所属字典类id不能为空")
    private String dictId;

    @Schema(description = "数据值")
    @NotBlank(message = "数据值不能为空")
    private String value;

    @Schema(description = "标签名")
    @NotBlank(message = "标签名不能为空")
    private String label;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序值，默认升序")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "备注信息")
    private String remarks;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;
}
