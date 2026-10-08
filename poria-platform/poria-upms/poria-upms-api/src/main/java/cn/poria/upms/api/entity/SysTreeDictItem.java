package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class SysTreeDictItem {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;

    @Schema(description = "字典ID")
    private String dictId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "简称")
    private String simpleName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "值")
    private String value;

    @Schema(description = "扩展字段")
    private String ext1;

    @Schema(description = "排序,最长6位")
    private Integer sort;

    @Schema(description = "上一级ID")
    private String pid;

    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "已删除 0:否 1:是")
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = " 创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改人")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;
}
