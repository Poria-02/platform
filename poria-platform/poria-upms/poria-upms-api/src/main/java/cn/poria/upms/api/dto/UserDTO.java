package cn.poria.upms.api.dto;

import cn.poria.upms.api.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
   description = "系统用户传输对象"
)
public class UserDTO extends SysUser {
   @Schema(
      description = "角色id集合"
   )
   private List<Long> role;
   @Schema(
      description = "部门id"
   )
   private Long deptId;
   @Schema(
      description = "新密码"
   )
   private String newpassword1;

   public List<Long> getRole() {
      return this.role;
   }

   public Long getDeptId() {
      return this.deptId;
   }

   public String getNewpassword1() {
      return this.newpassword1;
   }

   public void setRole(final List<Long> role) {
      this.role = role;
   }

   public void setDeptId(final Long deptId) {
      this.deptId = deptId;
   }

   public void setNewpassword1(final String newpassword1) {
      this.newpassword1 = newpassword1;
   }

   public String toString() {
      List var10000 = this.getRole();
      return "UserDTO(role=" + var10000 + ", deptId=" + this.getDeptId() + ", newpassword1=" + this.getNewpassword1() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UserDTO)) {
         return false;
      } else {
         UserDTO other = (UserDTO)o;
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

            Object this$role = this.getRole();
            Object other$role = other.getRole();
            if (this$role == null) {
               if (other$role != null) {
                  return false;
               }
            } else if (!this$role.equals(other$role)) {
               return false;
            }

            Object this$newpassword1 = this.getNewpassword1();
            Object other$newpassword1 = other.getNewpassword1();
            if (this$newpassword1 == null) {
               if (other$newpassword1 != null) {
                  return false;
               }
            } else if (!this$newpassword1.equals(other$newpassword1)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof UserDTO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $deptId = this.getDeptId();
      result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
      Object $role = this.getRole();
      result = result * 59 + ($role == null ? 43 : $role.hashCode());
      Object $newpassword1 = this.getNewpassword1();
      result = result * 59 + ($newpassword1 == null ? 43 : $newpassword1.hashCode());
      return result;
   }
}
