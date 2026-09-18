package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TreeDictVO {
   @Schema(
      description = "字典ID"
   )
   private String id;
   @Schema(
      description = "字典Code"
   )
   private @NotEmpty String code;
   @Schema(
      description = "名称"
   )
   private @NotEmpty String name;
   @Schema(
      description = "备注"
   )
   private String remark = "";
   @Schema(
      description = "类型 0：系统字典 1：业务字典"
   )
   private int type;
   @Schema(
      description = "字典内容类型 0：列表 1：树"
   )
   private @NotNull Boolean isTree;
   @Schema(
      description = "类型名称"
   )
   private String typeName;

   public String getId() {
      return this.id;
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public String getRemark() {
      return this.remark;
   }

   public int getType() {
      return this.type;
   }

   public Boolean getIsTree() {
      return this.isTree;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setType(final int type) {
      this.type = type;
   }

   public void setIsTree(final Boolean isTree) {
      this.isTree = isTree;
   }

   public void setTypeName(final String typeName) {
      this.typeName = typeName;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TreeDictVO)) {
         return false;
      } else {
         TreeDictVO other = (TreeDictVO)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.getType() != other.getType()) {
            return false;
         } else {
            Object this$isTree = this.getIsTree();
            Object other$isTree = other.getIsTree();
            if (this$isTree == null) {
               if (other$isTree != null) {
                  return false;
               }
            } else if (!this$isTree.equals(other$isTree)) {
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

            Object this$code = this.getCode();
            Object other$code = other.getCode();
            if (this$code == null) {
               if (other$code != null) {
                  return false;
               }
            } else if (!this$code.equals(other$code)) {
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

            Object this$remark = this.getRemark();
            Object other$remark = other.getRemark();
            if (this$remark == null) {
               if (other$remark != null) {
                  return false;
               }
            } else if (!this$remark.equals(other$remark)) {
               return false;
            }

            Object this$typeName = this.getTypeName();
            Object other$typeName = other.getTypeName();
            if (this$typeName == null) {
               if (other$typeName != null) {
                  return false;
               }
            } else if (!this$typeName.equals(other$typeName)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof TreeDictVO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getType();
      Object $isTree = this.getIsTree();
      result = result * 59 + ($isTree == null ? 43 : $isTree.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $code = this.getCode();
      result = result * 59 + ($code == null ? 43 : $code.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      Object $typeName = this.getTypeName();
      result = result * 59 + ($typeName == null ? 43 : $typeName.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getId();
      return "TreeDictVO(id=" + var10000 + ", code=" + this.getCode() + ", name=" + this.getName() + ", remark=" + this.getRemark() + ", type=" + this.getType() + ", isTree=" + this.getIsTree() + ", typeName=" + this.getTypeName() + ")";
   }
}
