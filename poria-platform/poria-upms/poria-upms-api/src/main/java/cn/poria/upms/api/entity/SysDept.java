package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "部门")
public class SysDept extends Model<SysDept> {
   private static final long serialVersionUID = 1L;

   @TableId(value = "dept_id", type = IdType.AUTO)
   @Schema(description = "部门id")
   private Long deptId;

   @Schema(description = "部门名称")
   @NotBlank(message = "部门名称不能为空")
   private String name;

   @Schema(description = "排序值")
   @NotNull(message = "排序值不能为空")
   private Integer sort;

   @Schema(description = "创建时间")
   private LocalDateTime createTime;

   @Schema(description = "修改时间")
   private LocalDateTime updateTime;

   @Schema(description = "父级部门id")
   private Long parentId;

   @TableLogic
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   @Schema(description = "部门类型  1-机构，2-机构下属科室")
   private Integer type;

   @Schema(description = "详情ID")
   private String detailId;

   public Long getDeptId() {
      return this.deptId;
   }

   public String getName() {
      return this.name;
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

   public Long getParentId() {
      return this.parentId;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   public Integer getType() {
      return this.type;
   }

   public String getDetailId() {
      return this.detailId;
   }

   public void setDeptId(final Long deptId) {
      this.deptId = deptId;
   }

   public void setName(final String name) {
      this.name = name;
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

   public void setParentId(final Long parentId) {
      this.parentId = parentId;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setDetailId(final String detailId) {
      this.detailId = detailId;
   }

   public String toString() {
      Long var10000 = this.getDeptId();
      return "SysDept(deptId=" + var10000 + ", name=" + this.getName() + ", sort=" + this.getSort() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", parentId=" + this.getParentId() + ", delFlag=" + this.getDelFlag() + ", type=" + this.getType() + ", detailId=" + this.getDetailId() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysDept)) {
         return false;
      } else {
         SysDept other = (SysDept)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$deptId = this.getDeptId();
            Object other$deptId = other.getDeptId();
            if (this$deptId == null) {
               if (other$deptId != null) {
                  return false;
               }
            } else if (!this$deptId.equals(other$deptId)) {
               return false;
            }

            Object this$sort = this.getSort();
            Object other$sort = other.getSort();
            if (this$sort == null) {
               if (other$sort != null) {
                  return false;
               }
            } else if (!this$sort.equals(other$sort)) {
               return false;
            }

            Object this$parentId = this.getParentId();
            Object other$parentId = other.getParentId();
            if (this$parentId == null) {
               if (other$parentId != null) {
                  return false;
               }
            } else if (!this$parentId.equals(other$parentId)) {
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

            Object this$updateTime = this.getUpdateTime();
            Object other$updateTime = other.getUpdateTime();
            if (this$updateTime == null) {
               if (other$updateTime != null) {
                  return false;
               }
            } else if (!this$updateTime.equals(other$updateTime)) {
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

            Object this$detailId = this.getDetailId();
            Object other$detailId = other.getDetailId();
            if (this$detailId == null) {
               if (other$detailId != null) {
                  return false;
               }
            } else if (!this$detailId.equals(other$detailId)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysDept;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $deptId = this.getDeptId();
      result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
      Object $sort = this.getSort();
      result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
      Object $parentId = this.getParentId();
      result = result * 59 + ($parentId == null ? 43 : $parentId.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      Object $detailId = this.getDetailId();
      result = result * 59 + ($detailId == null ? 43 : $detailId.hashCode());
      return result;
   }
}
