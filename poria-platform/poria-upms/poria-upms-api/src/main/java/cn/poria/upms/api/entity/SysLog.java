package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "日志")
public class SysLog implements Serializable {
   private static final long serialVersionUID = 1L;

   @TableId(type = IdType.AUTO)
   @Schema(description = "日志编号")
   private Long id;

   @Schema(description = "日志类型")
   @NotBlank(message = "日志类型不能为空")
   private String type;

   @Schema(description = "日志标题")
   @NotBlank(message = "日志标题不能为空")
   private String title;

   @Schema(description = "创建人")
   private String createBy;

   @Schema(description = "创建时间")
   private LocalDateTime createTime;

   @Schema(description = "更新时间")
   private LocalDateTime updateTime;

   @Schema(description = "操作ip地址")
   private String remoteAddr;

   @Schema(description = "用户代理")
   private String userAgent;

   @Schema(description = "请求uri")
   private String requestUri;

   @Schema(description = "操作方式")
   private String method;

   @Schema(description = "提交数据")
   private String params;

   @Schema(description = "方法执行时间")
   private Long time;

   @Schema(description = "异常信息")
   private String exception;

   @Schema(description = "应用标识")
   private String serviceId;

   @TableLogic
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   public Long getId() {
      return this.id;
   }

   public String getType() {
      return this.type;
   }

   public String getTitle() {
      return this.title;
   }

   public String getCreateBy() {
      return this.createBy;
   }

   public LocalDateTime getCreateTime() {
      return this.createTime;
   }

   public LocalDateTime getUpdateTime() {
      return this.updateTime;
   }

   public String getRemoteAddr() {
      return this.remoteAddr;
   }

   public String getUserAgent() {
      return this.userAgent;
   }

   public String getRequestUri() {
      return this.requestUri;
   }

   public String getMethod() {
      return this.method;
   }

   public String getParams() {
      return this.params;
   }

   public Long getTime() {
      return this.time;
   }

   public String getException() {
      return this.exception;
   }

   public String getServiceId() {
      return this.serviceId;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setCreateBy(final String createBy) {
      this.createBy = createBy;
   }

   public void setCreateTime(final LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final LocalDateTime updateTime) {
      this.updateTime = updateTime;
   }

   public void setRemoteAddr(final String remoteAddr) {
      this.remoteAddr = remoteAddr;
   }

   public void setUserAgent(final String userAgent) {
      this.userAgent = userAgent;
   }

   public void setRequestUri(final String requestUri) {
      this.requestUri = requestUri;
   }

   public void setMethod(final String method) {
      this.method = method;
   }

   public void setParams(final String params) {
      this.params = params;
   }

   public void setTime(final Long time) {
      this.time = time;
   }

   public void setException(final String exception) {
      this.exception = exception;
   }

   public void setServiceId(final String serviceId) {
      this.serviceId = serviceId;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysLog)) {
         return false;
      } else {
         SysLog other = (SysLog)o;
         if (!other.canEqual(this)) {
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

            Object this$time = this.getTime();
            Object other$time = other.getTime();
            if (this$time == null) {
               if (other$time != null) {
                  return false;
               }
            } else if (!this$time.equals(other$time)) {
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

            Object this$title = this.getTitle();
            Object other$title = other.getTitle();
            if (this$title == null) {
               if (other$title != null) {
                  return false;
               }
            } else if (!this$title.equals(other$title)) {
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

            Object this$updateTime = this.getUpdateTime();
            Object other$updateTime = other.getUpdateTime();
            if (this$updateTime == null) {
               if (other$updateTime != null) {
                  return false;
               }
            } else if (!this$updateTime.equals(other$updateTime)) {
               return false;
            }

            Object this$remoteAddr = this.getRemoteAddr();
            Object other$remoteAddr = other.getRemoteAddr();
            if (this$remoteAddr == null) {
               if (other$remoteAddr != null) {
                  return false;
               }
            } else if (!this$remoteAddr.equals(other$remoteAddr)) {
               return false;
            }

            Object this$userAgent = this.getUserAgent();
            Object other$userAgent = other.getUserAgent();
            if (this$userAgent == null) {
               if (other$userAgent != null) {
                  return false;
               }
            } else if (!this$userAgent.equals(other$userAgent)) {
               return false;
            }

            Object this$requestUri = this.getRequestUri();
            Object other$requestUri = other.getRequestUri();
            if (this$requestUri == null) {
               if (other$requestUri != null) {
                  return false;
               }
            } else if (!this$requestUri.equals(other$requestUri)) {
               return false;
            }

            Object this$method = this.getMethod();
            Object other$method = other.getMethod();
            if (this$method == null) {
               if (other$method != null) {
                  return false;
               }
            } else if (!this$method.equals(other$method)) {
               return false;
            }

            Object this$params = this.getParams();
            Object other$params = other.getParams();
            if (this$params == null) {
               if (other$params != null) {
                  return false;
               }
            } else if (!this$params.equals(other$params)) {
               return false;
            }

            Object this$exception = this.getException();
            Object other$exception = other.getException();
            if (this$exception == null) {
               if (other$exception != null) {
                  return false;
               }
            } else if (!this$exception.equals(other$exception)) {
               return false;
            }

            Object this$serviceId = this.getServiceId();
            Object other$serviceId = other.getServiceId();
            if (this$serviceId == null) {
               if (other$serviceId != null) {
                  return false;
               }
            } else if (!this$serviceId.equals(other$serviceId)) {
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
      return other instanceof SysLog;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $time = this.getTime();
      result = result * 59 + ($time == null ? 43 : $time.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $title = this.getTitle();
      result = result * 59 + ($title == null ? 43 : $title.hashCode());
      Object $createBy = this.getCreateBy();
      result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $remoteAddr = this.getRemoteAddr();
      result = result * 59 + ($remoteAddr == null ? 43 : $remoteAddr.hashCode());
      Object $userAgent = this.getUserAgent();
      result = result * 59 + ($userAgent == null ? 43 : $userAgent.hashCode());
      Object $requestUri = this.getRequestUri();
      result = result * 59 + ($requestUri == null ? 43 : $requestUri.hashCode());
      Object $method = this.getMethod();
      result = result * 59 + ($method == null ? 43 : $method.hashCode());
      Object $params = this.getParams();
      result = result * 59 + ($params == null ? 43 : $params.hashCode());
      Object $exception = this.getException();
      result = result * 59 + ($exception == null ? 43 : $exception.hashCode());
      Object $serviceId = this.getServiceId();
      result = result * 59 + ($serviceId == null ? 43 : $serviceId.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      return result;
   }

   public String toString() {
      Long var10000 = this.getId();
      return "SysLog(id=" + var10000 + ", type=" + this.getType() + ", title=" + this.getTitle() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", remoteAddr=" + this.getRemoteAddr() + ", userAgent=" + this.getUserAgent() + ", requestUri=" + this.getRequestUri() + ", method=" + this.getMethod() + ", params=" + this.getParams() + ", time=" + this.getTime() + ", exception=" + this.getException() + ", serviceId=" + this.getServiceId() + ", delFlag=" + this.getDelFlag() + ")";
   }
}
