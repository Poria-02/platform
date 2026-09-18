package cn.poria.upms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("auth")
public class AuthConfigProperties {
   public String encodeKey;
   public boolean enable;

   public String getEncodeKey() {
      return this.encodeKey;
   }

   public boolean isEnable() {
      return this.enable;
   }

   public void setEncodeKey(final String encodeKey) {
      this.encodeKey = encodeKey;
   }

   public void setEnable(final boolean enable) {
      this.enable = enable;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AuthConfigProperties)) {
         return false;
      } else {
         AuthConfigProperties other = (AuthConfigProperties)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.isEnable() != other.isEnable()) {
            return false;
         } else {
            Object this$encodeKey = this.getEncodeKey();
            Object other$encodeKey = other.getEncodeKey();
            if (this$encodeKey == null) {
               if (other$encodeKey != null) {
                  return false;
               }
            } else if (!this$encodeKey.equals(other$encodeKey)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AuthConfigProperties;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isEnable() ? 79 : 97);
      Object $encodeKey = this.getEncodeKey();
      result = result * 59 + ($encodeKey == null ? 43 : $encodeKey.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getEncodeKey();
      return "AuthConfigProperties(encodeKey=" + var10000 + ", enable=" + this.isEnable() + ")";
   }
}
