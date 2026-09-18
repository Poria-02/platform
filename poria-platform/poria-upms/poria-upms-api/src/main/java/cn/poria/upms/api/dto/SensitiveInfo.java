package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SensitiveInfo {
   @Schema(
      description = "手机号"
   )
   private String mobile;
   @Schema(
      description = "身份证号"
   )
   private String idCard;
   @Schema(
      description = "用户类型 1-用户，2-患者 3-医护"
   )
   private Integer userType;
   @Schema(
      description = "用户id"
   )
   private String userId;
   @Schema(
      description = "姓名"
   )
   private String name;
   @Schema(
      description = "证件类型（字典）"
   )
   private String idcardType;

   public String getMobile() {
      return this.mobile;
   }

   public String getIdCard() {
      return this.idCard;
   }

   public Integer getUserType() {
      return this.userType;
   }

   public String getUserId() {
      return this.userId;
   }

   public String getName() {
      return this.name;
   }

   public String getIdcardType() {
      return this.idcardType;
   }

   public void setMobile(final String mobile) {
      this.mobile = mobile;
   }

   public void setIdCard(final String idCard) {
      this.idCard = idCard;
   }

   public void setUserType(final Integer userType) {
      this.userType = userType;
   }

   public void setUserId(final String userId) {
      this.userId = userId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setIdcardType(final String idcardType) {
      this.idcardType = idcardType;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SensitiveInfo)) {
         return false;
      } else {
         SensitiveInfo other = (SensitiveInfo)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$userType = this.getUserType();
            Object other$userType = other.getUserType();
            if (this$userType == null) {
               if (other$userType != null) {
                  return false;
               }
            } else if (!this$userType.equals(other$userType)) {
               return false;
            }

            Object this$mobile = this.getMobile();
            Object other$mobile = other.getMobile();
            if (this$mobile == null) {
               if (other$mobile != null) {
                  return false;
               }
            } else if (!this$mobile.equals(other$mobile)) {
               return false;
            }

            Object this$idCard = this.getIdCard();
            Object other$idCard = other.getIdCard();
            if (this$idCard == null) {
               if (other$idCard != null) {
                  return false;
               }
            } else if (!this$idCard.equals(other$idCard)) {
               return false;
            }

            Object this$userId = this.getUserId();
            Object other$userId = other.getUserId();
            if (this$userId == null) {
               if (other$userId != null) {
                  return false;
               }
            } else if (!this$userId.equals(other$userId)) {
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

            Object this$idcardType = this.getIdcardType();
            Object other$idcardType = other.getIdcardType();
            if (this$idcardType == null) {
               if (other$idcardType != null) {
                  return false;
               }
            } else if (!this$idcardType.equals(other$idcardType)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SensitiveInfo;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $userType = this.getUserType();
      result = result * 59 + ($userType == null ? 43 : $userType.hashCode());
      Object $mobile = this.getMobile();
      result = result * 59 + ($mobile == null ? 43 : $mobile.hashCode());
      Object $idCard = this.getIdCard();
      result = result * 59 + ($idCard == null ? 43 : $idCard.hashCode());
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $idcardType = this.getIdcardType();
      result = result * 59 + ($idcardType == null ? 43 : $idcardType.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getMobile();
      return "SensitiveInfo(mobile=" + var10000 + ", idCard=" + this.getIdCard() + ", userType=" + this.getUserType() + ", userId=" + this.getUserId() + ", name=" + this.getName() + ", idcardType=" + this.getIdcardType() + ")";
   }
}
