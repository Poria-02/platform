package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "部门树"
)
public class DeptTree extends TreeNode {
   @Schema(
      description = "部门名称"
   )
   private String name;

   public String getName() {
      return this.name;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public String toString() {
      return "DeptTree(name=" + this.getName() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DeptTree)) {
         return false;
      } else {
         DeptTree other = (DeptTree)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof DeptTree;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      return result;
   }
}
