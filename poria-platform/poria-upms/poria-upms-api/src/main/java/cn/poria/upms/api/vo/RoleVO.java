package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "前端角色展示对象"
)
public class RoleVO {
   private Integer roleId;
   private String menuIds;

   public Integer getRoleId() {
      return this.roleId;
   }

   public String getMenuIds() {
      return this.menuIds;
   }

   public void setRoleId(final Integer roleId) {
      this.roleId = roleId;
   }

   public void setMenuIds(final String menuIds) {
      this.menuIds = menuIds;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RoleVO)) {
         return false;
      } else {
         RoleVO other = (RoleVO)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$roleId = this.getRoleId();
            Object other$roleId = other.getRoleId();
            if (this$roleId == null) {
               if (other$roleId != null) {
                  return false;
               }
            } else if (!this$roleId.equals(other$roleId)) {
               return false;
            }

            Object this$menuIds = this.getMenuIds();
            Object other$menuIds = other.getMenuIds();
            if (this$menuIds == null) {
               if (other$menuIds != null) {
                  return false;
               }
            } else if (!this$menuIds.equals(other$menuIds)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof RoleVO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $roleId = this.getRoleId();
      result = result * 59 + ($roleId == null ? 43 : $roleId.hashCode());
      Object $menuIds = this.getMenuIds();
      result = result * 59 + ($menuIds == null ? 43 : $menuIds.hashCode());
      return result;
   }

   public String toString() {
      Integer var10000 = this.getRoleId();
      return "RoleVO(roleId=" + var10000 + ", menuIds=" + this.getMenuIds() + ")";
   }
}
