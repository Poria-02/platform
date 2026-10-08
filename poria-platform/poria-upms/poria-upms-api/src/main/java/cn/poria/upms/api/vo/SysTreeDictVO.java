package cn.poria.upms.api.vo;

import cn.poria.upms.api.entity.SysTreeDictItem;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SysTreeDictVO {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "字典类型 0:系统字典 1:用户字典")
    private Integer type;

    @Schema(description = "是否为树形结构")
    private Integer isTree;

    @Schema(description = "租户ID")
    private Integer tenantId;

    private Integer deptId;

    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "已删除")
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改人")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "分级菜单下级")
    private List<SysTreeDictItem> treeItems;
}
