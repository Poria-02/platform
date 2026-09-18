package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "角色")
public class SysRole extends Model<SysRole> {
   private static final long serialVersionUID = 1L;

   @TableId(value = "role_id", type = IdType.AUTO)
   @Schema(description = "角色编号")
   private Long roleId;

   @Schema(description = "角色名称")
   @NotBlank(message = "角色名称不能为空")
   private String roleName;

   @Schema(description = "角色标识")
   @NotBlank(message = "角色标识不能为空")
   private String roleCode;

   @Schema(description = "角色描述")
   private String roleDesc;

   @Schema(description = "数据权限类型")
   @NotNull(message = "数据权限类型不能为空")
   private Integer dsType;

   @Schema(description = "数据权限作用范围")
   private String dsScope;

   @Schema(description = "创建时间")
   private LocalDateTime createTime;

   @Schema(description = "修改时间")
   private LocalDateTime updateTime;

   @TableLogic
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   public Long getRoleId() {
      return this.roleId;
   }

   public String getRoleName() {
      return this.roleName;
   }

   public String getRoleCode() {
      return this.roleCode;
   }

   public String getRoleDesc() {
      return this.roleDesc;
   }

   public Integer getDsType() {
      return this.dsType;
   }

   public String getDsScope() {
      return this.dsScope;
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

   public void setRoleId(final Long roleId) {
      this.roleId = roleId;
   }

   public void setRoleName(final String roleName) {
      this.roleName = roleName;
   }

   public void setRoleCode(final String roleCode) {
      this.roleCode = roleCode;
   }

   public void setRoleDesc(final String roleDesc) {
      this.roleDesc = roleDesc;
   }

   public void setDsType(final Integer dsType) {
      this.dsType = dsType;
   }

   public void setDsScope(final String dsScope) {
      this.dsScope = dsScope;
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
      Long var10000 = this.getRoleId();
      return "SysRole(roleId=" + var10000 + ", roleName=" + this.getRoleName() + ", roleCode=" + this.getRoleCode() + ", roleDesc=" + this.getRoleDesc() + ", dsType=" + this.getDsType() + ", dsScope=" + this.getDsScope() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysRole)) {
         return false;
      } else {
         SysRole other = (SysRole)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$roleId = this.getRoleId();
            Object other$roleId = other.getRoleId();
            if (this$roleId == null) {
               if (other$roleId != null) {
                  return false;
               }
            } else if (!this$roleId.equals(other$roleId)) {
               return false;
            }

            Object this$dsType = this.getDsType();
            Object other$dsType = other.getDsType();
            if (this$dsType == null) {
               if (other$dsType != null) {
                  return false;
               }
            } else if (!this$dsType.equals(other$dsType)) {
               return false;
            }

            Object this$roleName = this.getRoleName();
            Object other$roleName = other.getRoleName();
            if (this$roleName == null) {
               if (other$roleName != null) {
                  return false;
               }
            } else if (!this$roleName.equals(other$roleName)) {
               return false;
            }

            Object this$roleCode = this.getRoleCode();
            Object other$roleCode = other.getRoleCode();
            if (this$roleCode == null) {
               if (other$roleCode != null) {
                  return false;
               }
            } else if (!this$roleCode.equals(other$roleCode)) {
               return false;
            }

            Object this$roleDesc = this.getRoleDesc();
            Object other$roleDesc = other.getRoleDesc();
            if (this$roleDesc == null) {
               if (other$roleDesc != null) {
                  return false;
               }
            } else if (!this$roleDesc.equals(other$roleDesc)) {
               return false;
            }

            Object this$dsScope = this.getDsScope();
            Object other$dsScope = other.getDsScope();
            if (this$dsScope == null) {
               if (other$dsScope != null) {
                  return false;
               }
            } else if (!this$dsScope.equals(other$dsScope)) {
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
      return other instanceof SysRole;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $roleId = this.getRoleId();
      result = result * 59 + ($roleId == null ? 43 : $roleId.hashCode());
      Object $dsType = this.getDsType();
      result = result * 59 + ($dsType == null ? 43 : $dsType.hashCode());
      Object $roleName = this.getRoleName();
      result = result * 59 + ($roleName == null ? 43 : $roleName.hashCode());
      Object $roleCode = this.getRoleCode();
      result = result * 59 + ($roleCode == null ? 43 : $roleCode.hashCode());
      Object $roleDesc = this.getRoleDesc();
      result = result * 59 + ($roleDesc == null ? 43 : $roleDesc.hashCode());
      Object $dsScope = this.getDsScope();
      result = result * 59 + ($dsScope == null ? 43 : $dsScope.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      return result;
   }
}
