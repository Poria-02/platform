package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class DictQueryModel extends PageSearch {
   @Schema(
      description = "字典名称"
   )
   private String name;
   @Schema(
      description = "字典编号"
   )
   private String code;
   @Schema(
      description = "字典类型 0-系统字典，1-用户字典"
   )
   private String type;

   public String getName() {
      return this.name;
   }

   public String getCode() {
      return this.code;
   }

   public String getType() {
      return this.type;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DictQueryModel)) {
         return false;
      } else {
         DictQueryModel other = (DictQueryModel)o;
         if (!other.canEqual(this)) {
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

            Object this$code = this.getCode();
            Object other$code = other.getCode();
            if (this$code == null) {
               if (other$code != null) {
                  return false;
               }
            } else if (!this$code.equals(other$code)) {
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

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof DictQueryModel;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $code = this.getCode();
      result = result * 59 + ($code == null ? 43 : $code.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getName();
      return "DictQueryModel(name=" + var10000 + ", code=" + this.getCode() + ", type=" + this.getType() + ")";
   }
}
