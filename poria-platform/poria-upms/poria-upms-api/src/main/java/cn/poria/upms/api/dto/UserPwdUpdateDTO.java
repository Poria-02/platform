package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class UserPwdUpdateDTO {
   @Schema(
      description = "用户名"
   )
   private String username;
   @Schema(
      description = "原密码"
   )
   private @NotBlank(
   message = "原密码不能为空"
) String password;
   @Schema(
      description = "新密码"
   )
   private String newpassword1;

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getNewpassword1() {
      return this.newpassword1;
   }

   public void setUsername(final String username) {
      this.username = username;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   public void setNewpassword1(final String newpassword1) {
      this.newpassword1 = newpassword1;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UserPwdUpdateDTO)) {
         return false;
      } else {
         UserPwdUpdateDTO other = (UserPwdUpdateDTO)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$username = this.getUsername();
            Object other$username = other.getUsername();
            if (this$username == null) {
               if (other$username != null) {
                  return false;
               }
            } else if (!this$username.equals(other$username)) {
               return false;
            }

            Object this$password = this.getPassword();
            Object other$password = other.getPassword();
            if (this$password == null) {
               if (other$password != null) {
                  return false;
               }
            } else if (!this$password.equals(other$password)) {
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
      return other instanceof UserPwdUpdateDTO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $username = this.getUsername();
      result = result * 59 + ($username == null ? 43 : $username.hashCode());
      Object $password = this.getPassword();
      result = result * 59 + ($password == null ? 43 : $password.hashCode());
      Object $newpassword1 = this.getNewpassword1();
      result = result * 59 + ($newpassword1 == null ? 43 : $newpassword1.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getUsername();
      return "UserPwdUpdateDTO(username=" + var10000 + ", password=" + this.getPassword() + ", newpassword1=" + this.getNewpassword1() + ")";
   }
}
