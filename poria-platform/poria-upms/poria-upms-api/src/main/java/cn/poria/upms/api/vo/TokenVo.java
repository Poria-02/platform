package cn.poria.upms.api.vo;

public class TokenVo {
   private String id;
   private Long userId;
   private String clientId;
   private String username;
   private String accessToken;
   private String issuedAt;
   private String expiresAt;

   public String getId() {
      return this.id;
   }

   public Long getUserId() {
      return this.userId;
   }

   public String getClientId() {
      return this.clientId;
   }

   public String getUsername() {
      return this.username;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public String getIssuedAt() {
      return this.issuedAt;
   }

   public String getExpiresAt() {
      return this.expiresAt;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setUserId(final Long userId) {
      this.userId = userId;
   }

   public void setClientId(final String clientId) {
      this.clientId = clientId;
   }

   public void setUsername(final String username) {
      this.username = username;
   }

   public void setAccessToken(final String accessToken) {
      this.accessToken = accessToken;
   }

   public void setIssuedAt(final String issuedAt) {
      this.issuedAt = issuedAt;
   }

   public void setExpiresAt(final String expiresAt) {
      this.expiresAt = expiresAt;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TokenVo)) {
         return false;
      } else {
         TokenVo other = (TokenVo)o;
         if (!other.canEqual(this)) {
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

            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null) {
               if (other$id != null) {
                  return false;
               }
            } else if (!this$id.equals(other$id)) {
               return false;
            }

            Object this$clientId = this.getClientId();
            Object other$clientId = other.getClientId();
            if (this$clientId == null) {
               if (other$clientId != null) {
                  return false;
               }
            } else if (!this$clientId.equals(other$clientId)) {
               return false;
            }

            Object this$username = this.getUsername();
            Object other$username = other.getUsername();
            if (this$username == null) {
               if (other$username != null) {
                  return false;
               }
            } else if (!this$username.equals(other$username)) {
               return false;
            }

            Object this$accessToken = this.getAccessToken();
            Object other$accessToken = other.getAccessToken();
            if (this$accessToken == null) {
               if (other$accessToken != null) {
                  return false;
               }
            } else if (!this$accessToken.equals(other$accessToken)) {
               return false;
            }

            Object this$issuedAt = this.getIssuedAt();
            Object other$issuedAt = other.getIssuedAt();
            if (this$issuedAt == null) {
               if (other$issuedAt != null) {
                  return false;
               }
            } else if (!this$issuedAt.equals(other$issuedAt)) {
               return false;
            }

            Object this$expiresAt = this.getExpiresAt();
            Object other$expiresAt = other.getExpiresAt();
            if (this$expiresAt == null) {
               if (other$expiresAt != null) {
                  return false;
               }
            } else if (!this$expiresAt.equals(other$expiresAt)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof TokenVo;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $clientId = this.getClientId();
      result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
      Object $username = this.getUsername();
      result = result * 59 + ($username == null ? 43 : $username.hashCode());
      Object $accessToken = this.getAccessToken();
      result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
      Object $issuedAt = this.getIssuedAt();
      result = result * 59 + ($issuedAt == null ? 43 : $issuedAt.hashCode());
      Object $expiresAt = this.getExpiresAt();
      result = result * 59 + ($expiresAt == null ? 43 : $expiresAt.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getId();
      return "TokenVo(id=" + var10000 + ", userId=" + this.getUserId() + ", clientId=" + this.getClientId() + ", username=" + this.getUsername() + ", accessToken=" + this.getAccessToken() + ", issuedAt=" + this.getIssuedAt() + ", expiresAt=" + this.getExpiresAt() + ")";
   }
}
