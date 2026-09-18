package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public class SysTagItem extends Model<SysTagItem> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;

    @Schema(description = "标签分类id")
    private String tagId;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "标签值")
    private String value;

    @Schema(description = "标签icon地址")
    private String icon;

    @Schema(description = "标签颜色")
    private String color;

    @Schema(description = "状态1-启用，2-停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新时间")
    private Date updateTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新人")
    private String updateBy;

    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "是否删除0未删除，1-已删除")
    private Integer isDelete;

    public String getId() {
        return this.id;
    }

    public String getTagId() {
        return this.tagId;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getColor() {
        return this.color;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getRemark() {
        return this.remark;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public Integer getIsDelete() {
        return this.isDelete;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setTagId(final String tagId) {
        this.tagId = tagId;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateBy(final String createBy) {
        this.createBy = createBy;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateBy(final String updateBy) {
        this.updateBy = updateBy;
    }

    public void setIsDelete(final Integer isDelete) {
        this.isDelete = isDelete;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SysTagItem)) {
            return false;
        } else {
            SysTagItem other = (SysTagItem) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$status = this.getStatus();
                Object other$status = other.getStatus();
                if (this$status == null) {
                    if (other$status != null) {
                        return false;
                    }
                } else if (!this$status.equals(other$status)) {
                    return false;
                }

                Object this$isDelete = this.getIsDelete();
                Object other$isDelete = other.getIsDelete();
                if (this$isDelete == null) {
                    if (other$isDelete != null) {
                        return false;
                    }
                } else if (!this$isDelete.equals(other$isDelete)) {
                    return false;
                }

                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$tagId = this.getTagId();
                Object other$tagId = other.getTagId();
                if (this$tagId == null) {
                    if (other$tagId != null) {
                        return false;
                    }
                } else if (!this$tagId.equals(other$tagId)) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$value = this.getValue();
                Object other$value = other.getValue();
                if (this$value == null) {
                    if (other$value != null) {
                        return false;
                    }
                } else if (!this$value.equals(other$value)) {
                    return false;
                }

                Object this$icon = this.getIcon();
                Object other$icon = other.getIcon();
                if (this$icon == null) {
                    if (other$icon != null) {
                        return false;
                    }
                } else if (!this$icon.equals(other$icon)) {
                    return false;
                }

                Object this$color = this.getColor();
                Object other$color = other.getColor();
                if (this$color == null) {
                    if (other$color != null) {
                        return false;
                    }
                } else if (!this$color.equals(other$color)) {
                    return false;
                }

                Object this$remark = this.getRemark();
                Object other$remark = other.getRemark();
                if (this$remark == null) {
                    if (other$remark != null) {
                        return false;
                    }
                } else if (!this$remark.equals(other$remark)) {
                    return false;
                }

                Object this$createTime = this.getCreateTime();
                Object other$createTime = other.getCreateTime();
                if (this$createTime == null) {
                    if (other$createTime != null) {
                        return false;
                    }
                } else if (!this$createTime.equals(other$createTime)) {
                    return false;
                }

                Object this$createBy = this.getCreateBy();
                Object other$createBy = other.getCreateBy();
                if (this$createBy == null) {
                    if (other$createBy != null) {
                        return false;
                    }
                } else if (!this$createBy.equals(other$createBy)) {
                    return false;
                }

                Object this$updateTime = this.getUpdateTime();
                Object other$updateTime = other.getUpdateTime();
                if (this$updateTime == null) {
                    if (other$updateTime != null) {
                        return false;
                    }
                } else if (!this$updateTime.equals(other$updateTime)) {
                    return false;
                }

                Object this$updateBy = this.getUpdateBy();
                Object other$updateBy = other.getUpdateBy();
                if (this$updateBy == null) {
                    if (other$updateBy != null) {
                        return false;
                    }
                } else if (!this$updateBy.equals(other$updateBy)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SysTagItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $isDelete = this.getIsDelete();
        result = result * 59 + ($isDelete == null ? 43 : $isDelete.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $tagId = this.getTagId();
        result = result * 59 + ($tagId == null ? 43 : $tagId.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        Object $icon = this.getIcon();
        result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
        Object $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : $color.hashCode());
        Object $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        Object $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        Object $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        Object $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
        Object $updateBy = this.getUpdateBy();
        result = result * 59 + ($updateBy == null ? 43 : $updateBy.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getId();
        return "SysTagItem(id=" + var10000 + ", tagId=" + this.getTagId() + ", name=" + this.getName() + ", value=" + this.getValue() + ", icon=" + this.getIcon() + ", color=" + this.getColor() + ", status=" + this.getStatus() + ", remark=" + this.getRemark() + ", createTime=" + this.getCreateTime() + ", createBy=" + this.getCreateBy() + ", updateTime=" + this.getUpdateTime() + ", updateBy=" + this.getUpdateBy() + ", isDelete=" + this.getIsDelete() + ")";
    }
}
