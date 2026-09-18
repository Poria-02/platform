package cn.poria.upms.api.dto;

import cn.poria.upms.api.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Arrays;

@Schema(
   description = "用户信息"
)
public class UserInfo implements Serializable {
   @Schema(
      description = "用户基本信息"
   )
   private SysUser sysUser;
   @Schema(
      description = "权限标识集合"
   )
   private String[] permissions;
   @Schema(
      description = "角色标识集合"
   )
   private Long[] roles;

   public SysUser getSysUser() {
      return this.sysUser;
   }

   public String[] getPermissions() {
      return this.permissions;
   }

   public Long[] getRoles() {
      return this.roles;
   }

   public void setSysUser(final SysUser sysUser) {
      this.sysUser = sysUser;
   }

   public void setPermissions(final String[] permissions) {
      this.permissions = permissions;
   }

   public void setRoles(final Long[] roles) {
      this.roles = roles;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UserInfo)) {
         return false;
      } else {
         UserInfo other = (UserInfo)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$sysUser = this.getSysUser();
            Object other$sysUser = other.getSysUser();
            if (this$sysUser == null) {
               if (other$sysUser != null) {
                  return false;
               }
            } else if (!this$sysUser.equals(other$sysUser)) {
               return false;
            }

            if (!Arrays.deepEquals(this.getPermissions(), other.getPermissions())) {
               return false;
            } else if (!Arrays.deepEquals(this.getRoles(), other.getRoles())) {
               return false;
            } else {
               return true;
            }
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof UserInfo;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $sysUser = this.getSysUser();
      result = result * 59 + ($sysUser == null ? 43 : $sysUser.hashCode());
      result = result * 59 + Arrays.deepHashCode(this.getPermissions());
      result = result * 59 + Arrays.deepHashCode(this.getRoles());
      return result;
   }

   public String toString() {
      SysUser var10000 = this.getSysUser();
      return "UserInfo(sysUser=" + var10000 + ", permissions=" + Arrays.deepToString(this.getPermissions()) + ", roles=" + Arrays.deepToString(this.getRoles()) + ")";
   }
}
