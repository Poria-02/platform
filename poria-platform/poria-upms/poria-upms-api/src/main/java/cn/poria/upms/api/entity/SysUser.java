package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class SysUser implements Serializable {
   private static final long serialVersionUID = 1L;

   @TableId(value = "user_id", type = IdType.AUTO)
   @Schema(description = "主键id")
   private Long userId;

   @Schema(description = "用户名")
   private String username;

   @Schema(description = "密码")
   private String password;

   @JsonIgnore
   @Schema(description = "随机盐")
   private String salt;

   @Schema(description = "创建时间")
   private LocalDateTime createTime;

   @Schema(description = "修改时间")
   private LocalDateTime updateTime;

   @TableLogic
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   @Schema(description = "锁定标记")
   private String lockFlag;

   @Schema(description = "手机号")
   private String phone;

   @Schema(description = "头像地址")
   private String avatar;

   @Schema(description = "用户所属部门id")
   private Long deptId;

   @Schema(description = "用户所属租户id")
   private Integer tenantId;

   @Schema(description = "微信openid")
   private String wxOpenid;

   @Schema(description = "微信小程序openid")
   private String miniOpenid;

   @Schema(description = "QQ openid")
   private String qqOpenid;

   @Schema(description = "码云唯一标识")
   private String giteeLogin;

   @Schema(description = "开源中国唯一标识")
   private String oscId;

   @Schema(description = "网易云信Token")
   private String yxToken;

   private Date lastPwdUpdateTime;

   public Long getUserId() {
      return this.userId;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getSalt() {
      return this.salt;
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

   public String getLockFlag() {
      return this.lockFlag;
   }

   public String getPhone() {
      return this.phone;
   }

   public String getAvatar() {
      return this.avatar;
   }

   public Long getDeptId() {
      return this.deptId;
   }

   public Integer getTenantId() {
      return this.tenantId;
   }

   public String getWxOpenid() {
      return this.wxOpenid;
   }

   public String getMiniOpenid() {
      return this.miniOpenid;
   }

   public String getQqOpenid() {
      return this.qqOpenid;
   }

   public String getGiteeLogin() {
      return this.giteeLogin;
   }

   public String getOscId() {
      return this.oscId;
   }

   public String getYxToken() {
      return this.yxToken;
   }

   public Date getLastPwdUpdateTime() {
      return this.lastPwdUpdateTime;
   }

   public void setUserId(final Long userId) {
      this.userId = userId;
   }

   public void setUsername(final String username) {
      this.username = username;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   @JsonIgnore
   public void setSalt(final String salt) {
      this.salt = salt;
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

   public void setLockFlag(final String lockFlag) {
      this.lockFlag = lockFlag;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   public void setAvatar(final String avatar) {
      this.avatar = avatar;
   }

   public void setDeptId(final Long deptId) {
      this.deptId = deptId;
   }

   public void setTenantId(final Integer tenantId) {
      this.tenantId = tenantId;
   }

   public void setWxOpenid(final String wxOpenid) {
      this.wxOpenid = wxOpenid;
   }

   public void setMiniOpenid(final String miniOpenid) {
      this.miniOpenid = miniOpenid;
   }

   public void setQqOpenid(final String qqOpenid) {
      this.qqOpenid = qqOpenid;
   }

   public void setGiteeLogin(final String giteeLogin) {
      this.giteeLogin = giteeLogin;
   }

   public void setOscId(final String oscId) {
      this.oscId = oscId;
   }

   public void setYxToken(final String yxToken) {
      this.yxToken = yxToken;
   }

   public void setLastPwdUpdateTime(final Date lastPwdUpdateTime) {
      this.lastPwdUpdateTime = lastPwdUpdateTime;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysUser)) {
         return false;
      } else {
         SysUser other = (SysUser)o;
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

            Object this$deptId = this.getDeptId();
            Object other$deptId = other.getDeptId();
            if (this$deptId == null) {
               if (other$deptId != null) {
                  return false;
               }
            } else if (!this$deptId.equals(other$deptId)) {
               return false;
            }

            Object this$tenantId = this.getTenantId();
            Object other$tenantId = other.getTenantId();
            if (this$tenantId == null) {
               if (other$tenantId != null) {
                  return false;
               }
            } else if (!this$tenantId.equals(other$tenantId)) {
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

            Object this$password = this.getPassword();
            Object other$password = other.getPassword();
            if (this$password == null) {
               if (other$password != null) {
                  return false;
               }
            } else if (!this$password.equals(other$password)) {
               return false;
            }

            Object this$salt = this.getSalt();
            Object other$salt = other.getSalt();
            if (this$salt == null) {
               if (other$salt != null) {
                  return false;
               }
            } else if (!this$salt.equals(other$salt)) {
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

            Object this$lockFlag = this.getLockFlag();
            Object other$lockFlag = other.getLockFlag();
            if (this$lockFlag == null) {
               if (other$lockFlag != null) {
                  return false;
               }
            } else if (!this$lockFlag.equals(other$lockFlag)) {
               return false;
            }

            Object this$phone = this.getPhone();
            Object other$phone = other.getPhone();
            if (this$phone == null) {
               if (other$phone != null) {
                  return false;
               }
            } else if (!this$phone.equals(other$phone)) {
               return false;
            }

            Object this$avatar = this.getAvatar();
            Object other$avatar = other.getAvatar();
            if (this$avatar == null) {
               if (other$avatar != null) {
                  return false;
               }
            } else if (!this$avatar.equals(other$avatar)) {
               return false;
            }

            Object this$wxOpenid = this.getWxOpenid();
            Object other$wxOpenid = other.getWxOpenid();
            if (this$wxOpenid == null) {
               if (other$wxOpenid != null) {
                  return false;
               }
            } else if (!this$wxOpenid.equals(other$wxOpenid)) {
               return false;
            }

            Object this$miniOpenid = this.getMiniOpenid();
            Object other$miniOpenid = other.getMiniOpenid();
            if (this$miniOpenid == null) {
               if (other$miniOpenid != null) {
                  return false;
               }
            } else if (!this$miniOpenid.equals(other$miniOpenid)) {
               return false;
            }

            Object this$qqOpenid = this.getQqOpenid();
            Object other$qqOpenid = other.getQqOpenid();
            if (this$qqOpenid == null) {
               if (other$qqOpenid != null) {
                  return false;
               }
            } else if (!this$qqOpenid.equals(other$qqOpenid)) {
               return false;
            }

            Object this$giteeLogin = this.getGiteeLogin();
            Object other$giteeLogin = other.getGiteeLogin();
            if (this$giteeLogin == null) {
               if (other$giteeLogin != null) {
                  return false;
               }
            } else if (!this$giteeLogin.equals(other$giteeLogin)) {
               return false;
            }

            Object this$oscId = this.getOscId();
            Object other$oscId = other.getOscId();
            if (this$oscId == null) {
               if (other$oscId != null) {
                  return false;
               }
            } else if (!this$oscId.equals(other$oscId)) {
               return false;
            }

            Object this$yxToken = this.getYxToken();
            Object other$yxToken = other.getYxToken();
            if (this$yxToken == null) {
               if (other$yxToken != null) {
                  return false;
               }
            } else if (!this$yxToken.equals(other$yxToken)) {
               return false;
            }

            Object this$lastPwdUpdateTime = this.getLastPwdUpdateTime();
            Object other$lastPwdUpdateTime = other.getLastPwdUpdateTime();
            if (this$lastPwdUpdateTime == null) {
               if (other$lastPwdUpdateTime != null) {
                  return false;
               }
            } else if (!this$lastPwdUpdateTime.equals(other$lastPwdUpdateTime)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysUser;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $deptId = this.getDeptId();
      result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
      Object $tenantId = this.getTenantId();
      result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
      Object $username = this.getUsername();
      result = result * 59 + ($username == null ? 43 : $username.hashCode());
      Object $password = this.getPassword();
      result = result * 59 + ($password == null ? 43 : $password.hashCode());
      Object $salt = this.getSalt();
      result = result * 59 + ($salt == null ? 43 : $salt.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      Object $lockFlag = this.getLockFlag();
      result = result * 59 + ($lockFlag == null ? 43 : $lockFlag.hashCode());
      Object $phone = this.getPhone();
      result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
      Object $avatar = this.getAvatar();
      result = result * 59 + ($avatar == null ? 43 : $avatar.hashCode());
      Object $wxOpenid = this.getWxOpenid();
      result = result * 59 + ($wxOpenid == null ? 43 : $wxOpenid.hashCode());
      Object $miniOpenid = this.getMiniOpenid();
      result = result * 59 + ($miniOpenid == null ? 43 : $miniOpenid.hashCode());
      Object $qqOpenid = this.getQqOpenid();
      result = result * 59 + ($qqOpenid == null ? 43 : $qqOpenid.hashCode());
      Object $giteeLogin = this.getGiteeLogin();
      result = result * 59 + ($giteeLogin == null ? 43 : $giteeLogin.hashCode());
      Object $oscId = this.getOscId();
      result = result * 59 + ($oscId == null ? 43 : $oscId.hashCode());
      Object $yxToken = this.getYxToken();
      result = result * 59 + ($yxToken == null ? 43 : $yxToken.hashCode());
      Object $lastPwdUpdateTime = this.getLastPwdUpdateTime();
      result = result * 59 + ($lastPwdUpdateTime == null ? 43 : $lastPwdUpdateTime.hashCode());
      return result;
   }

   public String toString() {
      Long var10000 = this.getUserId();
      return "SysUser(userId=" + var10000 + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", salt=" + this.getSalt() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ", lockFlag=" + this.getLockFlag() + ", phone=" + this.getPhone() + ", avatar=" + this.getAvatar() + ", deptId=" + this.getDeptId() + ", tenantId=" + this.getTenantId() + ", wxOpenid=" + this.getWxOpenid() + ", miniOpenid=" + this.getMiniOpenid() + ", qqOpenid=" + this.getQqOpenid() + ", giteeLogin=" + this.getGiteeLogin() + ", oscId=" + this.getOscId() + ", yxToken=" + this.getYxToken() + ", lastPwdUpdateTime=" + this.getLastPwdUpdateTime() + ")";
   }
}
