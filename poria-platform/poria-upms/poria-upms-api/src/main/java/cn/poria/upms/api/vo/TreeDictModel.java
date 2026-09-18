package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class TreeDictModel {
   @Schema(
      description = "字典ID"
   )
   private @NotBlank(
   message = "id不能为空"
) String id;
   @Schema(
      description = "名称"
   )
   private @NotBlank(
   message = "名称不能为空"
) String name;
   @Schema(
      description = "备注"
   )
   private String remark = "";

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TreeDictModel)) {
         return false;
      } else {
         TreeDictModel other = (TreeDictModel)o;
         if (!other.canEqual(this)) {
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

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof TreeDictModel;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getId();
      return "TreeDictModel(id=" + var10000 + ", name=" + this.getName() + ", remark=" + this.getRemark() + ")";
   }
}
