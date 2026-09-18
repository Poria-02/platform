package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class SysTag extends Model<SysTag> {

   @TableId(type = IdType.ASSIGN_ID)
   @Schema(description = "id")
   private String id;

   @Schema(description = "标签key(唯一)")
   private String tagKey;

   @Schema(description = "标签分类名称")
   private String name;

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

   public String getTagKey() {
      return this.tagKey;
   }

   public String getName() {
      return this.name;
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

   public void setTagKey(final String tagKey) {
      this.tagKey = tagKey;
   }

   public void setName(final String name) {
      this.name = name;
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
      } else if (!(o instanceof SysTag)) {
         return false;
      } else {
         SysTag other = (SysTag)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
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

            Object this$tagKey = this.getTagKey();
            Object other$tagKey = other.getTagKey();
            if (this$tagKey == null) {
               if (other$tagKey != null) {
                  return false;
               }
            } else if (!this$tagKey.equals(other$tagKey)) {
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
      return other instanceof SysTag;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $isDelete = this.getIsDelete();
      result = result * 59 + ($isDelete == null ? 43 : $isDelete.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $tagKey = this.getTagKey();
      result = result * 59 + ($tagKey == null ? 43 : $tagKey.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
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
      return "SysTag(id=" + var10000 + ", tagKey=" + this.getTagKey() + ", name=" + this.getName() + ", createTime=" + this.getCreateTime() + ", createBy=" + this.getCreateBy() + ", updateTime=" + this.getUpdateTime() + ", updateBy=" + this.getUpdateBy() + ", isDelete=" + this.getIsDelete() + ")";
   }
}
