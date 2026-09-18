package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

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

   public String getId() {
      return this.id;
   }

   public String getDictId() {
      return this.dictId;
   }

   public String getName() {
      return this.name;
   }

   public String getSimpleName() {
      return this.simpleName;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getValue() {
      return this.value;
   }

   public String getExt1() {
      return this.ext1;
   }

   public Integer getSort() {
      return this.sort;
   }

   public String getPid() {
      return this.pid;
   }

   public Integer getIsDelete() {
      return this.isDelete;
   }

   public String getCreateBy() {
      return this.createBy;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public String getUpdateBy() {
      return this.updateBy;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setDictId(final String dictId) {
      this.dictId = dictId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSimpleName(final String simpleName) {
      this.simpleName = simpleName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setValue(final String value) {
      this.value = value;
   }

   public void setExt1(final String ext1) {
      this.ext1 = ext1;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }

   public void setPid(final String pid) {
      this.pid = pid;
   }

   public void setIsDelete(final Integer isDelete) {
      this.isDelete = isDelete;
   }

   public void setCreateBy(final String createBy) {
      this.createBy = createBy;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateBy(final String updateBy) {
      this.updateBy = updateBy;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysTreeDictItem)) {
         return false;
      } else {
         SysTreeDictItem other = (SysTreeDictItem)o;
         if (!other.canEqual(this)) {
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

            Object this$dictId = this.getDictId();
            Object other$dictId = other.getDictId();
            if (this$dictId == null) {
               if (other$dictId != null) {
                  return false;
               }
            } else if (!this$dictId.equals(other$dictId)) {
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

            Object this$simpleName = this.getSimpleName();
            Object other$simpleName = other.getSimpleName();
            if (this$simpleName == null) {
               if (other$simpleName != null) {
                  return false;
               }
            } else if (!this$simpleName.equals(other$simpleName)) {
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

            Object this$value = this.getValue();
            Object other$value = other.getValue();
            if (this$value == null) {
               if (other$value != null) {
                  return false;
               }
            } else if (!this$value.equals(other$value)) {
               return false;
            }

            Object this$ext1 = this.getExt1();
            Object other$ext1 = other.getExt1();
            if (this$ext1 == null) {
               if (other$ext1 != null) {
                  return false;
               }
            } else if (!this$ext1.equals(other$ext1)) {
               return false;
            }

            Object this$pid = this.getPid();
            Object other$pid = other.getPid();
            if (this$pid == null) {
               if (other$pid != null) {
                  return false;
               }
            } else if (!this$pid.equals(other$pid)) {
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

            Object this$createTime = this.getCreateTime();
            Object other$createTime = other.getCreateTime();
            if (this$createTime == null) {
               if (other$createTime != null) {
                  return false;
               }
            } else if (!this$createTime.equals(other$createTime)) {
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

            Object this$updateTime = this.getUpdateTime();
            Object other$updateTime = other.getUpdateTime();
            if (this$updateTime == null) {
               if (other$updateTime != null) {
                  return false;
               }
            } else if (!this$updateTime.equals(other$updateTime)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysTreeDictItem;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $sort = this.getSort();
      result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
      Object $isDelete = this.getIsDelete();
      result = result * 59 + ($isDelete == null ? 43 : $isDelete.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $dictId = this.getDictId();
      result = result * 59 + ($dictId == null ? 43 : $dictId.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $simpleName = this.getSimpleName();
      result = result * 59 + ($simpleName == null ? 43 : $simpleName.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      Object $value = this.getValue();
      result = result * 59 + ($value == null ? 43 : $value.hashCode());
      Object $ext1 = this.getExt1();
      result = result * 59 + ($ext1 == null ? 43 : $ext1.hashCode());
      Object $pid = this.getPid();
      result = result * 59 + ($pid == null ? 43 : $pid.hashCode());
      Object $createBy = this.getCreateBy();
      result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateBy = this.getUpdateBy();
      result = result * 59 + ($updateBy == null ? 43 : $updateBy.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getId();
      return "SysTreeDictItem(id=" + var10000 + ", dictId=" + this.getDictId() + ", name=" + this.getName() + ", simpleName=" + this.getSimpleName() + ", remark=" + this.getRemark() + ", value=" + this.getValue() + ", ext1=" + this.getExt1() + ", sort=" + this.getSort() + ", pid=" + this.getPid() + ", isDelete=" + this.getIsDelete() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ")";
   }
}
