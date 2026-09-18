package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class SysSensitiveLog {

   @TableId(type = IdType.ASSIGN_ID)
   private String id;

   @TableField(fill = FieldFill.INSERT)
   @Schema(description = "创建人姓名")
   private String createBy;

   @TableField(fill = FieldFill.INSERT)
   @Schema(description = "创建时间")
   private Date createTime;

   @TableLogic(value = "0", delval = "1")
   @TableField(fill = FieldFill.INSERT)
   @Schema(description = "是否删除0未删除，1-已删除")
   private Integer isDelete;

   @Schema(description = "敏感信息")
   private String sensitiveInfo;

   @Schema(description = "用户类型 1-用户，2-患者 3-医护")
   private Integer userType;

   @Schema(description = "查看人姓名")
   private String createName;

   public String getId() {
      return this.id;
   }

   public String getCreateBy() {
      return this.createBy;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Integer getIsDelete() {
      return this.isDelete;
   }

   public String getSensitiveInfo() {
      return this.sensitiveInfo;
   }

   public Integer getUserType() {
      return this.userType;
   }

   public String getCreateName() {
      return this.createName;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setCreateBy(final String createBy) {
      this.createBy = createBy;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setIsDelete(final Integer isDelete) {
      this.isDelete = isDelete;
   }

   public void setSensitiveInfo(final String sensitiveInfo) {
      this.sensitiveInfo = sensitiveInfo;
   }

   public void setUserType(final Integer userType) {
      this.userType = userType;
   }

   public void setCreateName(final String createName) {
      this.createName = createName;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysSensitiveLog)) {
         return false;
      } else {
         SysSensitiveLog other = (SysSensitiveLog)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$isDelete = this.getIsDelete();
            Object other$isDelete = other.getIsDelete();
            if (this$isDelete == null) {
               if (other$isDelete != null) {
                  return false;
               }
            } else if (!this$isDelete.equals(other$isDelete)) {
               return false;
            }

            Object this$userType = this.getUserType();
            Object other$userType = other.getUserType();
            if (this$userType == null) {
               if (other$userType != null) {
                  return false;
               }
            } else if (!this$userType.equals(other$userType)) {
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

            Object this$createBy = this.getCreateBy();
            Object other$createBy = other.getCreateBy();
            if (this$createBy == null) {
               if (other$createBy != null) {
                  return false;
               }
            } else if (!this$createBy.equals(other$createBy)) {
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

            Object this$sensitiveInfo = this.getSensitiveInfo();
            Object other$sensitiveInfo = other.getSensitiveInfo();
            if (this$sensitiveInfo == null) {
               if (other$sensitiveInfo != null) {
                  return false;
               }
            } else if (!this$sensitiveInfo.equals(other$sensitiveInfo)) {
               return false;
            }

            Object this$createName = this.getCreateName();
            Object other$createName = other.getCreateName();
            if (this$createName == null) {
               if (other$createName != null) {
                  return false;
               }
            } else if (!this$createName.equals(other$createName)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysSensitiveLog;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $isDelete = this.getIsDelete();
      result = result * 59 + ($isDelete == null ? 43 : $isDelete.hashCode());
      Object $userType = this.getUserType();
      result = result * 59 + ($userType == null ? 43 : $userType.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $createBy = this.getCreateBy();
      result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $sensitiveInfo = this.getSensitiveInfo();
      result = result * 59 + ($sensitiveInfo == null ? 43 : $sensitiveInfo.hashCode());
      Object $createName = this.getCreateName();
      result = result * 59 + ($createName == null ? 43 : $createName.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getId();
      return "SysSensitiveLog(id=" + var10000 + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", isDelete=" + this.getIsDelete() + ", sensitiveInfo=" + this.getSensitiveInfo() + ", userType=" + this.getUserType() + ", createName=" + this.getCreateName() + ")";
   }
}
