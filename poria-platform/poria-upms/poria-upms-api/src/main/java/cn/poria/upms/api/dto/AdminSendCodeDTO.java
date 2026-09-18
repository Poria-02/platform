package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AdminSendCodeDTO {
   @Schema(
      description = "手机号"
   )
   private @NotBlank(
   message = "手机号不能为空"
) String mobile;
   @Schema(
      description = "用户类型"
   )
   private @NotBlank(
   message = "类型不能为空"
) String type;
   @Schema(
      description = "随机码"
   )
   private @NotBlank(
   message = "随机码不能为空"
) String randomStr;
   @Schema(
      description = "图片验证码"
   )
   private @NotBlank(
   message = "验证码不能为空"
) String verifyCode;

   public String getMobile() {
      return this.mobile;
   }

   public String getType() {
      return this.type;
   }

   public String getRandomStr() {
      return this.randomStr;
   }

   public String getVerifyCode() {
      return this.verifyCode;
   }

   public void setMobile(final String mobile) {
      this.mobile = mobile;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setRandomStr(final String randomStr) {
      this.randomStr = randomStr;
   }

   public void setVerifyCode(final String verifyCode) {
      this.verifyCode = verifyCode;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AdminSendCodeDTO)) {
         return false;
      } else {
         AdminSendCodeDTO other = (AdminSendCodeDTO)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$mobile = this.getMobile();
            Object other$mobile = other.getMobile();
            if (this$mobile == null) {
               if (other$mobile != null) {
                  return false;
               }
            } else if (!this$mobile.equals(other$mobile)) {
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

            Object this$randomStr = this.getRandomStr();
            Object other$randomStr = other.getRandomStr();
            if (this$randomStr == null) {
               if (other$randomStr != null) {
                  return false;
               }
            } else if (!this$randomStr.equals(other$randomStr)) {
               return false;
            }

            Object this$verifyCode = this.getVerifyCode();
            Object other$verifyCode = other.getVerifyCode();
            if (this$verifyCode == null) {
               if (other$verifyCode != null) {
                  return false;
               }
            } else if (!this$verifyCode.equals(other$verifyCode)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AdminSendCodeDTO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mobile = this.getMobile();
      result = result * 59 + ($mobile == null ? 43 : $mobile.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $randomStr = this.getRandomStr();
      result = result * 59 + ($randomStr == null ? 43 : $randomStr.hashCode());
      Object $verifyCode = this.getVerifyCode();
      result = result * 59 + ($verifyCode == null ? 43 : $verifyCode.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getMobile();
      return "AdminSendCodeDTO(mobile=" + var10000 + ", type=" + this.getType() + ", randomStr=" + this.getRandomStr() + ", verifyCode=" + this.getVerifyCode() + ")";
   }
}
