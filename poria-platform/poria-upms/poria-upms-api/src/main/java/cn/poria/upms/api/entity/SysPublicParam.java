package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(description = "公共参数")
public class SysPublicParam extends Model<SysPublicParam> {
   private static final long serialVersionUID = 1L;

   @TableId(type = IdType.AUTO)
   @Schema(description = "公共参数编号")
   private String publicId;

   @Schema(description = "公共参数名称", required = true, example = "公共参数名称")
   private String publicName;

   @Schema(description = "键[英文大写+下划线]", required = true, example = "PIGX_PUBLIC_KEY")
   private String publicKey;

   @Schema(description = "值", required = true, example = "999")
   private String publicValue;

   @Schema(description = "标识[1有效；2无效]", example = "1")
   private String status;

   @TableLogic
   @Schema(description = "状态[0-正常，1-删除]", example = "0")
   private String delFlag;

   @Schema(description = "编码", example = "^(PIG|PIGX)$")
   private String validateCode;

   @Schema(description = "创建时间", example = "2019-03-21 12:28:48")
   private Date createTime;

   @Schema(description = "修改时间", example = "2019-03-21 12:28:48")
   private Date updateTime;

   @TableField("`system`")
   @Schema(description = "是否是系统内置")
   private String system;

   @Schema(description = "类型[1-检索；2-原文...]", example = "1")
   private String publicType;

   public String getPublicId() {
      return this.publicId;
   }

   public String getPublicName() {
      return this.publicName;
   }

   public String getPublicKey() {
      return this.publicKey;
   }

   public String getPublicValue() {
      return this.publicValue;
   }

   public String getStatus() {
      return this.status;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   public String getValidateCode() {
      return this.validateCode;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public String getSystem() {
      return this.system;
   }

   public String getPublicType() {
      return this.publicType;
   }

   public void setPublicId(final String publicId) {
      this.publicId = publicId;
   }

   public void setPublicName(final String publicName) {
      this.publicName = publicName;
   }

   public void setPublicKey(final String publicKey) {
      this.publicKey = publicKey;
   }

   public void setPublicValue(final String publicValue) {
      this.publicValue = publicValue;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public void setValidateCode(final String validateCode) {
      this.validateCode = validateCode;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setSystem(final String system) {
      this.system = system;
   }

   public void setPublicType(final String publicType) {
      this.publicType = publicType;
   }

   public String toString() {
      String var10000 = this.getPublicId();
      return "SysPublicParam(publicId=" + var10000 + ", publicName=" + this.getPublicName() + ", publicKey=" + this.getPublicKey() + ", publicValue=" + this.getPublicValue() + ", status=" + this.getStatus() + ", delFlag=" + this.getDelFlag() + ", validateCode=" + this.getValidateCode() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", system=" + this.getSystem() + ", publicType=" + this.getPublicType() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysPublicParam)) {
         return false;
      } else {
         SysPublicParam other = (SysPublicParam)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$publicId = this.getPublicId();
            Object other$publicId = other.getPublicId();
            if (this$publicId == null) {
               if (other$publicId != null) {
                  return false;
               }
            } else if (!this$publicId.equals(other$publicId)) {
               return false;
            }

            Object this$publicName = this.getPublicName();
            Object other$publicName = other.getPublicName();
            if (this$publicName == null) {
               if (other$publicName != null) {
                  return false;
               }
            } else if (!this$publicName.equals(other$publicName)) {
               return false;
            }

            Object this$publicKey = this.getPublicKey();
            Object other$publicKey = other.getPublicKey();
            if (this$publicKey == null) {
               if (other$publicKey != null) {
                  return false;
               }
            } else if (!this$publicKey.equals(other$publicKey)) {
               return false;
            }

            Object this$publicValue = this.getPublicValue();
            Object other$publicValue = other.getPublicValue();
            if (this$publicValue == null) {
               if (other$publicValue != null) {
                  return false;
               }
            } else if (!this$publicValue.equals(other$publicValue)) {
               return false;
            }

            Object this$status = this.getStatus();
            Object other$status = other.getStatus();
            if (this$status == null) {
               if (other$status != null) {
                  return false;
               }
            } else if (!this$status.equals(other$status)) {
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

            Object this$validateCode = this.getValidateCode();
            Object other$validateCode = other.getValidateCode();
            if (this$validateCode == null) {
               if (other$validateCode != null) {
                  return false;
               }
            } else if (!this$validateCode.equals(other$validateCode)) {
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

            Object this$system = this.getSystem();
            Object other$system = other.getSystem();
            if (this$system == null) {
               if (other$system != null) {
                  return false;
               }
            } else if (!this$system.equals(other$system)) {
               return false;
            }

            Object this$publicType = this.getPublicType();
            Object other$publicType = other.getPublicType();
            if (this$publicType == null) {
               if (other$publicType != null) {
                  return false;
               }
            } else if (!this$publicType.equals(other$publicType)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysPublicParam;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $publicId = this.getPublicId();
      result = result * 59 + ($publicId == null ? 43 : $publicId.hashCode());
      Object $publicName = this.getPublicName();
      result = result * 59 + ($publicName == null ? 43 : $publicName.hashCode());
      Object $publicKey = this.getPublicKey();
      result = result * 59 + ($publicKey == null ? 43 : $publicKey.hashCode());
      Object $publicValue = this.getPublicValue();
      result = result * 59 + ($publicValue == null ? 43 : $publicValue.hashCode());
      Object $status = this.getStatus();
      result = result * 59 + ($status == null ? 43 : $status.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      Object $validateCode = this.getValidateCode();
      result = result * 59 + ($validateCode == null ? 43 : $validateCode.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $system = this.getSystem();
      result = result * 59 + ($system == null ? 43 : $system.hashCode());
      Object $publicType = this.getPublicType();
      result = result * 59 + ($publicType == null ? 43 : $publicType.hashCode());
      return result;
   }
}
