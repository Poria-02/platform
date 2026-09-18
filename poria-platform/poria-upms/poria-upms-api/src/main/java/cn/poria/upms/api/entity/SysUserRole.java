package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户角色")
public class SysUserRole extends Model<SysUserRole> {
   private static final long serialVersionUID = 1L;

   @Schema(description = "用户id")
   private Long userId;

   @Schema(description = "角色id")
   private Long roleId;

   public Long getUserId() {
      return this.userId;
   }

   public Long getRoleId() {
      return this.roleId;
   }

   public void setUserId(final Long userId) {
      this.userId = userId;
   }

   public void setRoleId(final Long roleId) {
      this.roleId = roleId;
   }

   public String toString() {
      Long var10000 = this.getUserId();
      return "SysUserRole(userId=" + var10000 + ", roleId=" + this.getRoleId() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysUserRole)) {
         return false;
      } else {
         SysUserRole other = (SysUserRole)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$userId = this.getUserId();
            Object other$userId = other.getUserId();
            if (this$userId == null) {
               if (other$userId != null) {
                  return false;
               }
            } else if (!this$userId.equals(other$userId)) {
               return false;
            }

            Object this$roleId = this.getRoleId();
            Object other$roleId = other.getRoleId();
            if (this$roleId == null) {
               if (other$roleId != null) {
                  return false;
               }
            } else if (!this$roleId.equals(other$roleId)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysUserRole;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $roleId = this.getRoleId();
      result = result * 59 + ($roleId == null ? 43 : $roleId.hashCode());
      return result;
   }
}
