package cn.poria.upms.api.vo;

import cn.poria.upms.api.entity.SysDictItem;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典类型")
public class SysDictVO extends Model<SysDictVO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "字典编号")
    private String id;

    @Schema(description = "字典类型")
    private String type;

    @TableField(condition = "%s LIKE CONCAT(CONCAT('%%',#{%s}),'%%')")
    @Schema(description = "字典描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField("`system`")
    @Schema(description = "是否系统内置")
    private String system;

    @Schema(description = "备注信息")
    private String remarks;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "字典项值")
    private List<SysDictItem> items;
}
