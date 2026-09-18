package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "字典项")
public class SysDictItem extends Model<SysDictItem> {

   private static final long serialVersionUID = 1L;

   @TableId(type = IdType.ASSIGN_ID)
   @Schema(description = "字典项id")
   private String id;

   @Schema(description = "所属字典类id")
   private String dictId;

   @Schema(description = "数据值")
   private String value;

   @Schema(description = "标签名")
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

   public String getId() {
      return this.id;
   }

   public String getDictId() {
      return this.dictId;
   }

   public String getValue() {
      return this.value;
   }

   public String getLabel() {
      return this.label;
   }

   public String getType() {
      return this.type;
   }

   public String getDescription() {
      return this.description;
   }

   public Integer getSort() {
      return this.sort;
   }

   public LocalDateTime getCreateTime() {
      return this.createTime;
   }

   public LocalDateTime getUpdateTime() {
      return this.updateTime;
   }

   public String getRemarks() {
      return this.remarks;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setDictId(final String dictId) {
      this.dictId = dictId;
   }

   public void setValue(final String value) {
      this.value = value;
   }

   public void setLabel(final String label) {
      this.label = label;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }

   public void setCreateTime(final LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final LocalDateTime updateTime) {
      this.updateTime = updateTime;
   }

   public void setRemarks(final String remarks) {
      this.remarks = remarks;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public String toString() {
      String var10000 = this.getId();
      return "SysDictItem(id=" + var10000 + ", dictId=" + this.getDictId() + ", value=" + this.getValue() + ", label=" + this.getLabel() + ", type=" + this.getType() + ", description=" + this.getDescription() + ", sort=" + this.getSort() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", remarks=" + this.getRemarks() + ", delFlag=" + this.getDelFlag() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysDictItem)) {
         return false;
      } else {
         SysDictItem other = (SysDictItem)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$sort = this.getSort();
            Object other$sort = other.getSort();
            if (this$sort == null) {
               if (other$sort != null) {
                  return false;
               }
            } else if (!this$sort.equals(other$sort)) {
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

            Object this$dictId = this.getDictId();
            Object other$dictId = other.getDictId();
            if (this$dictId == null) {
               if (other$dictId != null) {
                  return false;
               }
            } else if (!this$dictId.equals(other$dictId)) {
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

            Object this$label = this.getLabel();
            Object other$label = other.getLabel();
            if (this$label == null) {
               if (other$label != null) {
                  return false;
               }
            } else if (!this$label.equals(other$label)) {
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

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysDictItem;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $sort = this.getSort();
      result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $dictId = this.getDictId();
      result = result * 59 + ($dictId == null ? 43 : $dictId.hashCode());
      Object $value = this.getValue();
      result = result * 59 + ($value == null ? 43 : $value.hashCode());
      Object $label = this.getLabel();
      result = result * 59 + ($label == null ? 43 : $label.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $description = this.getDescription();
      result = result * 59 + ($description == null ? 43 : $description.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $remarks = this.getRemarks();
      result = result * 59 + ($remarks == null ? 43 : $remarks.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      return result;
   }
}
