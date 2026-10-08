package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门关系")
public class SysDeptRelation extends Model<SysDeptRelation> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "祖先节点")
    private Long ancestor;

    @Schema(description = "后代节点")
    private Long descendant;
}
