package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "前端日志展示对象"
)
public class PreLogVO {
   @Schema(
      description = "请求url"
   )
   private String url;
   @Schema(
      description = "请求耗时"
   )
   private String time;
   @Schema(
      description = "请求用户"
   )
   private String user;
   @Schema(
      description = "请求结果0:成功9:失败"
   )
   private String type;
   @Schema(
      description = "请求传递参数"
   )
   private String message;
   @Schema(
      description = "异常信息"
   )
   private String stack;
   @Schema(
      description = "日志标题"
   )
   private String info;

   public String getUrl() {
      return this.url;
   }

   public String getTime() {
      return this.time;
   }

   public String getUser() {
      return this.user;
   }

   public String getType() {
      return this.type;
   }

   public String getMessage() {
      return this.message;
   }

   public String getStack() {
      return this.stack;
   }

   public String getInfo() {
      return this.info;
   }

   public void setUrl(final String url) {
      this.url = url;
   }

   public void setTime(final String time) {
      this.time = time;
   }

   public void setUser(final String user) {
      this.user = user;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setMessage(final String message) {
      this.message = message;
   }

   public void setStack(final String stack) {
      this.stack = stack;
   }

   public void setInfo(final String info) {
      this.info = info;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PreLogVO)) {
         return false;
      } else {
         PreLogVO other = (PreLogVO)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$url = this.getUrl();
            Object other$url = other.getUrl();
            if (this$url == null) {
               if (other$url != null) {
                  return false;
               }
            } else if (!this$url.equals(other$url)) {
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

            Object this$user = this.getUser();
            Object other$user = other.getUser();
            if (this$user == null) {
               if (other$user != null) {
                  return false;
               }
            } else if (!this$user.equals(other$user)) {
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

            Object this$message = this.getMessage();
            Object other$message = other.getMessage();
            if (this$message == null) {
               if (other$message != null) {
                  return false;
               }
            } else if (!this$message.equals(other$message)) {
               return false;
            }

            Object this$stack = this.getStack();
            Object other$stack = other.getStack();
            if (this$stack == null) {
               if (other$stack != null) {
                  return false;
               }
            } else if (!this$stack.equals(other$stack)) {
               return false;
            }

            Object this$info = this.getInfo();
            Object other$info = other.getInfo();
            if (this$info == null) {
               if (other$info != null) {
                  return false;
               }
            } else if (!this$info.equals(other$info)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof PreLogVO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $url = this.getUrl();
      result = result * 59 + ($url == null ? 43 : $url.hashCode());
      Object $time = this.getTime();
      result = result * 59 + ($time == null ? 43 : $time.hashCode());
      Object $user = this.getUser();
      result = result * 59 + ($user == null ? 43 : $user.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $message = this.getMessage();
      result = result * 59 + ($message == null ? 43 : $message.hashCode());
      Object $stack = this.getStack();
      result = result * 59 + ($stack == null ? 43 : $stack.hashCode());
      Object $info = this.getInfo();
      result = result * 59 + ($info == null ? 43 : $info.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getUrl();
      return "PreLogVO(url=" + var10000 + ", time=" + this.getTime() + ", user=" + this.getUser() + ", type=" + this.getType() + ", message=" + this.getMessage() + ", stack=" + this.getStack() + ", info=" + this.getInfo() + ")";
   }
}
