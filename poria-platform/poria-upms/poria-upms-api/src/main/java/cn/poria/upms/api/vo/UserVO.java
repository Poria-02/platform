package cn.poria.upms.api.vo;

import cn.poria.common.core.sensitive.Sensitive;
import cn.poria.common.core.sensitive.SensitiveTypeEnum;
import cn.poria.upms.api.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(
   description = "前端用户展示对象"
)
public class UserVO implements Serializable {
   private static final long serialVersionUID = 1L;
   @Schema(
      description = "主键"
   )
   private Long userId;
   @Schema(
      description = "用户名"
   )
   private String username;
   @Schema(
      description = "密码"
   )
   private String password;
   @Schema(
      description = "随机盐"
   )
   private String salt;
   @Schema(
      description = "微信open id"
   )
   private String wxOpenid;
   @Schema(
      description = "qq open id"
   )
   private String qqOpenid;
   @Schema(
      description = "创建时间"
   )
   private LocalDateTime createTime;
   @Schema(
      description = "修改时间"
   )
   private LocalDateTime updateTime;
   @Schema(
      description = "删除标记,1:已删除,0:正常"
   )
   private String delFlag;
   @Schema(
      description = "锁定标记,0:正常,9:已锁定"
   )
   private String lockFlag;
   @Sensitive(
      type = SensitiveTypeEnum.MOBILE_PHONE
   )
   @Schema(
      description = "手机号"
   )
   private String phone;
   @Schema(
      description = "头像"
   )
   private String avatar;
   @Schema(
      description = "所属部门"
   )
   private Integer deptId;
   @Schema(
      description = "所属租户"
   )
   private Integer tenantId;
   @Schema(
      description = "所属部门名称"
   )
   private String deptName;
   @Schema(
      description = "管理端最后登陆时间"
   )
   private String lastLoginTimeManager;
   @Schema(
      description = "运营端最后登陆时间"
   )
   private String lastLoginTimeOperate;
   @Schema(
      description = "拥有的角色列表"
   )
   private List<SysRole> roleList;

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

   public String getWxOpenid() {
      return this.wxOpenid;
   }

   public String getQqOpenid() {
      return this.qqOpenid;
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

   public Integer getDeptId() {
      return this.deptId;
   }

   public Integer getTenantId() {
      return this.tenantId;
   }

   public String getDeptName() {
      return this.deptName;
   }

   public String getLastLoginTimeManager() {
      return this.lastLoginTimeManager;
   }

   public String getLastLoginTimeOperate() {
      return this.lastLoginTimeOperate;
   }

   public List<SysRole> getRoleList() {
      return this.roleList;
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

   public void setSalt(final String salt) {
      this.salt = salt;
   }

   public void setWxOpenid(final String wxOpenid) {
      this.wxOpenid = wxOpenid;
   }

   public void setQqOpenid(final String qqOpenid) {
      this.qqOpenid = qqOpenid;
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

   public void setDeptId(final Integer deptId) {
      this.deptId = deptId;
   }

   public void setTenantId(final Integer tenantId) {
      this.tenantId = tenantId;
   }

   public void setDeptName(final String deptName) {
      this.deptName = deptName;
   }

   public void setLastLoginTimeManager(final String lastLoginTimeManager) {
      this.lastLoginTimeManager = lastLoginTimeManager;
   }

   public void setLastLoginTimeOperate(final String lastLoginTimeOperate) {
      this.lastLoginTimeOperate = lastLoginTimeOperate;
   }

   public void setRoleList(final List<SysRole> roleList) {
      this.roleList = roleList;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UserVO)) {
         return false;
      } else {
         UserVO other = (UserVO)o;
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

            Object this$wxOpenid = this.getWxOpenid();
            Object other$wxOpenid = other.getWxOpenid();
            if (this$wxOpenid == null) {
               if (other$wxOpenid != null) {
                  return false;
               }
            } else if (!this$wxOpenid.equals(other$wxOpenid)) {
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

            Object this$deptName = this.getDeptName();
            Object other$deptName = other.getDeptName();
            if (this$deptName == null) {
               if (other$deptName != null) {
                  return false;
               }
            } else if (!this$deptName.equals(other$deptName)) {
               return false;
            }

            Object this$lastLoginTimeManager = this.getLastLoginTimeManager();
            Object other$lastLoginTimeManager = other.getLastLoginTimeManager();
            if (this$lastLoginTimeManager == null) {
               if (other$lastLoginTimeManager != null) {
                  return false;
               }
            } else if (!this$lastLoginTimeManager.equals(other$lastLoginTimeManager)) {
               return false;
            }

            Object this$lastLoginTimeOperate = this.getLastLoginTimeOperate();
            Object other$lastLoginTimeOperate = other.getLastLoginTimeOperate();
            if (this$lastLoginTimeOperate == null) {
               if (other$lastLoginTimeOperate != null) {
                  return false;
               }
            } else if (!this$lastLoginTimeOperate.equals(other$lastLoginTimeOperate)) {
               return false;
            }

            Object this$roleList = this.getRoleList();
            Object other$roleList = other.getRoleList();
            if (this$roleList == null) {
               if (other$roleList != null) {
                  return false;
               }
            } else if (!this$roleList.equals(other$roleList)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof UserVO;
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
      Object $wxOpenid = this.getWxOpenid();
      result = result * 59 + ($wxOpenid == null ? 43 : $wxOpenid.hashCode());
      Object $qqOpenid = this.getQqOpenid();
      result = result * 59 + ($qqOpenid == null ? 43 : $qqOpenid.hashCode());
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
      Object $deptName = this.getDeptName();
      result = result * 59 + ($deptName == null ? 43 : $deptName.hashCode());
      Object $lastLoginTimeManager = this.getLastLoginTimeManager();
      result = result * 59 + ($lastLoginTimeManager == null ? 43 : $lastLoginTimeManager.hashCode());
      Object $lastLoginTimeOperate = this.getLastLoginTimeOperate();
      result = result * 59 + ($lastLoginTimeOperate == null ? 43 : $lastLoginTimeOperate.hashCode());
      Object $roleList = this.getRoleList();
      result = result * 59 + ($roleList == null ? 43 : $roleList.hashCode());
      return result;
   }

   public String toString() {
      Long var10000 = this.getUserId();
      return "UserVO(userId=" + var10000 + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", salt=" + this.getSalt() + ", wxOpenid=" + this.getWxOpenid() + ", qqOpenid=" + this.getQqOpenid() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ", lockFlag=" + this.getLockFlag() + ", phone=" + this.getPhone() + ", avatar=" + this.getAvatar() + ", deptId=" + this.getDeptId() + ", tenantId=" + this.getTenantId() + ", deptName=" + this.getDeptName() + ", lastLoginTimeManager=" + this.getLastLoginTimeManager() + ", lastLoginTimeOperate=" + this.getLastLoginTimeOperate() + ", roleList=" + this.getRoleList() + ")";
   }
}
