package cn.poria.upms.api.vo;

import cn.poria.upms.api.entity.SysDictItem;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(
   description = "字典类型"
)
public class SysDictVO extends Model<SysDictVO> implements Serializable {
   private static final long serialVersionUID = 1L;
   @TableId(
      type = IdType.ASSIGN_ID
   )
   @Schema(
      description = "字典编号"
   )
   private String id;
   @Schema(
      description = "字典类型"
   )
   private String type;
   @TableField(
      condition = "%s LIKE CONCAT(CONCAT('%%',#{%s}),'%%')"
   )
   @Schema(
      description = "字典描述"
   )
   private String description;
   @Schema(
      description = "创建时间"
   )
   private LocalDateTime createTime;
   @Schema(
      description = "更新时间"
   )
   private LocalDateTime updateTime;
   @TableField("`system`")
   @Schema(
      description = "是否系统内置"
   )
   private String system;
   @Schema(
      description = "备注信息"
   )
   private String remarks;
   @TableLogic
   @Schema(
      description = "删除标记,1:已删除,0:正常"
   )
   private String delFlag;
   @Schema(
      description = "字典项值"
   )
   private List<SysDictItem> items;

   public String getId() {
      return this.id;
   }

   public String getType() {
      return this.type;
   }

   public String getDescription() {
      return this.description;
   }

   public LocalDateTime getCreateTime() {
      return this.createTime;
   }

   public LocalDateTime getUpdateTime() {
      return this.updateTime;
   }

   public String getSystem() {
      return this.system;
   }

   public String getRemarks() {
      return this.remarks;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   public List<SysDictItem> getItems() {
      return this.items;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setCreateTime(final LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final LocalDateTime updateTime) {
      this.updateTime = updateTime;
   }

   public void setSystem(final String system) {
      this.system = system;
   }

   public void setRemarks(final String remarks) {
      this.remarks = remarks;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public void setItems(final List<SysDictItem> items) {
      this.items = items;
   }

   public String toString() {
      String var10000 = this.getId();
      return "SysDictVO(id=" + var10000 + ", type=" + this.getType() + ", description=" + this.getDescription() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", system=" + this.getSystem() + ", remarks=" + this.getRemarks() + ", delFlag=" + this.getDelFlag() + ", items=" + this.getItems() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysDictVO)) {
         return false;
      } else {
         SysDictVO other = (SysDictVO)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null) {
               if (other$id != null) {
                  return false;
               }
            } else if (!this$id.equals(other$id)) {
               return false;
            }

            Object this$type = this.getType();
            Object other$type = other.getType();
            if (this$type == null) {
               if (other$type != null) {
                  return false;
               }
            } else if (!this$type.equals(other$type)) {
               return false;
            }

            Object this$description = this.getDescription();
            Object other$description = other.getDescription();
            if (this$description == null) {
               if (other$description != null) {
                  return false;
               }
            } else if (!this$description.equals(other$description)) {
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

            Object this$updateTime = this.getUpdateTime();
            Object other$updateTime = other.getUpdateTime();
            if (this$updateTime == null) {
               if (other$updateTime != null) {
                  return false;
               }
            } else if (!this$updateTime.equals(other$updateTime)) {
               return false;
            }

            Object this$system = this.getSystem();
            Object other$system = other.getSystem();
            if (this$system == null) {
               if (other$system != null) {
                  return false;
               }
            } else if (!this$system.equals(other$system)) {
               return false;
            }

            Object this$remarks = this.getRemarks();
            Object other$remarks = other.getRemarks();
            if (this$remarks == null) {
               if (other$remarks != null) {
                  return false;
               }
            } else if (!this$remarks.equals(other$remarks)) {
               return false;
            }

            Object this$delFlag = this.getDelFlag();
            Object other$delFlag = other.getDelFlag();
            if (this$delFlag == null) {
               if (other$delFlag != null) {
                  return false;
               }
            } else if (!this$delFlag.equals(other$delFlag)) {
               return false;
            }

            Object this$items = this.getItems();
            Object other$items = other.getItems();
            if (this$items == null) {
               if (other$items != null) {
                  return false;
               }
            } else if (!this$items.equals(other$items)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysDictVO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $description = this.getDescription();
      result = result * 59 + ($description == null ? 43 : $description.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $system = this.getSystem();
      result = result * 59 + ($system == null ? 43 : $system.hashCode());
      Object $remarks = this.getRemarks();
      result = result * 59 + ($remarks == null ? 43 : $remarks.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      Object $items = this.getItems();
      result = result * 59 + ($items == null ? 43 : $items.hashCode());
      return result;
   }
}
