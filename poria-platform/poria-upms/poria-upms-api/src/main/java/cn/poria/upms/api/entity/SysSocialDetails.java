package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Schema(description = "第三方账号信息")
public class SysSocialDetails extends Model<SysSocialDetails> {
   private static final long serialVersionUID = 1L;

   @TableId
   @Schema(description = "主键")
   private Integer id;

   @Schema(description = "账号类型")
   @NotBlank(message = "类型不能为空")
   private String type;

   @Schema(description = "描述")
   private String remark;

   @Schema(description = "appId")
   @NotBlank(message = "账号不能为空")
   private String appId;

   @Schema(description = "app secret")
   @NotBlank(message = "密钥不能为空")
   private String appSecret;

   @Schema(description = "回调地址")
   private String redirectUrl;

   @Schema(description = "创建时间")
   private LocalDateTime createTime;

   @Schema(description = "更新时间")
   private LocalDateTime updateTime;

   @TableLogic
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   public Integer getId() {
      return this.id;
   }

   public String getType() {
      return this.type;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getAppId() {
      return this.appId;
   }

   public String getAppSecret() {
      return this.appSecret;
   }

   public String getRedirectUrl() {
      return this.redirectUrl;
   }

   public LocalDateTime getCreateTime() {
      return this.createTime;
   }

   public LocalDateTime getUpdateTime() {
      return this.updateTime;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   public void setId(final Integer id) {
      this.id = id;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setAppId(final String appId) {
      this.appId = appId;
   }

   public void setAppSecret(final String appSecret) {
      this.appSecret = appSecret;
   }

   public void setRedirectUrl(final String redirectUrl) {
      this.redirectUrl = redirectUrl;
   }

   public void setCreateTime(final LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final LocalDateTime updateTime) {
      this.updateTime = updateTime;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public String toString() {
      Integer var10000 = this.getId();
      return "SysSocialDetails(id=" + var10000 + ", type=" + this.getType() + ", remark=" + this.getRemark() + ", appId=" + this.getAppId() + ", appSecret=" + this.getAppSecret() + ", redirectUrl=" + this.getRedirectUrl() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysSocialDetails)) {
         return false;
      } else {
         SysSocialDetails other = (SysSocialDetails)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null) {
               if (other$id != null) {
                  return false;
               }
            } else if (!this$id.equals(other$id)) {
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

            Object this$remark = this.getRemark();
            Object other$remark = other.getRemark();
            if (this$remark == null) {
               if (other$remark != null) {
                  return false;
               }
            } else if (!this$remark.equals(other$remark)) {
               return false;
            }

            Object this$appId = this.getAppId();
            Object other$appId = other.getAppId();
            if (this$appId == null) {
               if (other$appId != null) {
                  return false;
               }
            } else if (!this$appId.equals(other$appId)) {
               return false;
            }

            Object this$appSecret = this.getAppSecret();
            Object other$appSecret = other.getAppSecret();
            if (this$appSecret == null) {
               if (other$appSecret != null) {
                  return false;
               }
            } else if (!this$appSecret.equals(other$appSecret)) {
               return false;
            }

            Object this$redirectUrl = this.getRedirectUrl();
            Object other$redirectUrl = other.getRedirectUrl();
            if (this$redirectUrl == null) {
               if (other$redirectUrl != null) {
                  return false;
               }
            } else if (!this$redirectUrl.equals(other$redirectUrl)) {
               return false;
            }

            Object this$createTime = this.getCreateTime();
            Object other$createTime = other.getCreateTime();
            if (this$createTime == null) {
               if (other$createTime != null) {
                  return false;
               }
            } else if (!this$createTime.equals(other$createTime)) {
               return false;
            }

            Object this$updateTime = this.getUpdateTime();
            Object other$updateTime = other.getUpdateTime();
            if (this$updateTime == null) {
               if (other$updateTime != null) {
                  return false;
               }
            } else if (!this$updateTime.equals(other$updateTime)) {
               return false;
            }

            Object this$delFlag = this.getDelFlag();
            Object other$delFlag = other.getDelFlag();
            if (this$delFlag == null) {
               if (other$delFlag != null) {
                  return false;
               }
            } else if (!this$delFlag.equals(other$delFlag)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysSocialDetails;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      Object $appId = this.getAppId();
      result = result * 59 + ($appId == null ? 43 : $appId.hashCode());
      Object $appSecret = this.getAppSecret();
      result = result * 59 + ($appSecret == null ? 43 : $appSecret.hashCode());
      Object $redirectUrl = this.getRedirectUrl();
      result = result * 59 + ($redirectUrl == null ? 43 : $redirectUrl.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      return result;
   }
}
