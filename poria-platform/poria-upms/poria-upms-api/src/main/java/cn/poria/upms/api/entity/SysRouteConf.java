package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(description = "网关路由信息")
public class SysRouteConf extends Model<SysRouteConf> {
   private static final long serialVersionUID = 1L;

   @JsonIgnore
   @TableId(type = IdType.AUTO)
   @Schema(description = "主键")
   private Integer id;

   @Schema(description = "路由id")
   private String routeId;

   @Schema(description = "路由名称")
   private String routeName;

   @Schema(description = "断言")
   private String predicates;

   @Schema(description = "过滤器")
   private String filters;

   @Schema(description = "请求uri")
   private String uri;

   @TableField("`order`")
   @Schema(description = "排序值")
   private Integer order;

   @Schema(description = "创建时间")
   private Date createTime;

   @Schema(description = "修改时间")
   private Date updateTime;

   @Schema(description = "元数据")
   private String metadata;

   @TableLogic(value = "0", delval = "1")
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   public Integer getId() {
      return this.id;
   }

   public String getRouteId() {
      return this.routeId;
   }

   public String getRouteName() {
      return this.routeName;
   }

   public String getPredicates() {
      return this.predicates;
   }

   public String getFilters() {
      return this.filters;
   }

   public String getUri() {
      return this.uri;
   }

   public Integer getOrder() {
      return this.order;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public String getMetadata() {
      return this.metadata;
   }

   public String getDelFlag() {
      return this.delFlag;
   }

   @JsonIgnore
   public void setId(final Integer id) {
      this.id = id;
   }

   public void setRouteId(final String routeId) {
      this.routeId = routeId;
   }

   public void setRouteName(final String routeName) {
      this.routeName = routeName;
   }

   public void setPredicates(final String predicates) {
      this.predicates = predicates;
   }

   public void setFilters(final String filters) {
      this.filters = filters;
   }

   public void setUri(final String uri) {
      this.uri = uri;
   }

   public void setOrder(final Integer order) {
      this.order = order;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setMetadata(final String metadata) {
      this.metadata = metadata;
   }

   public void setDelFlag(final String delFlag) {
      this.delFlag = delFlag;
   }

   public String toString() {
      Integer var10000 = this.getId();
      return "SysRouteConf(id=" + var10000 + ", routeId=" + this.getRouteId() + ", routeName=" + this.getRouteName() + ", predicates=" + this.getPredicates() + ", filters=" + this.getFilters() + ", uri=" + this.getUri() + ", order=" + this.getOrder() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", metadata=" + this.getMetadata() + ", delFlag=" + this.getDelFlag() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysRouteConf)) {
         return false;
      } else {
         SysRouteConf other = (SysRouteConf)o;
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

            Object this$order = this.getOrder();
            Object other$order = other.getOrder();
            if (this$order == null) {
               if (other$order != null) {
                  return false;
               }
            } else if (!this$order.equals(other$order)) {
               return false;
            }

            Object this$routeId = this.getRouteId();
            Object other$routeId = other.getRouteId();
            if (this$routeId == null) {
               if (other$routeId != null) {
                  return false;
               }
            } else if (!this$routeId.equals(other$routeId)) {
               return false;
            }

            Object this$routeName = this.getRouteName();
            Object other$routeName = other.getRouteName();
            if (this$routeName == null) {
               if (other$routeName != null) {
                  return false;
               }
            } else if (!this$routeName.equals(other$routeName)) {
               return false;
            }

            Object this$predicates = this.getPredicates();
            Object other$predicates = other.getPredicates();
            if (this$predicates == null) {
               if (other$predicates != null) {
                  return false;
               }
            } else if (!this$predicates.equals(other$predicates)) {
               return false;
            }

            Object this$filters = this.getFilters();
            Object other$filters = other.getFilters();
            if (this$filters == null) {
               if (other$filters != null) {
                  return false;
               }
            } else if (!this$filters.equals(other$filters)) {
               return false;
            }

            Object this$uri = this.getUri();
            Object other$uri = other.getUri();
            if (this$uri == null) {
               if (other$uri != null) {
                  return false;
               }
            } else if (!this$uri.equals(other$uri)) {
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

            Object this$metadata = this.getMetadata();
            Object other$metadata = other.getMetadata();
            if (this$metadata == null) {
               if (other$metadata != null) {
                  return false;
               }
            } else if (!this$metadata.equals(other$metadata)) {
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
      return other instanceof SysRouteConf;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $order = this.getOrder();
      result = result * 59 + ($order == null ? 43 : $order.hashCode());
      Object $routeId = this.getRouteId();
      result = result * 59 + ($routeId == null ? 43 : $routeId.hashCode());
      Object $routeName = this.getRouteName();
      result = result * 59 + ($routeName == null ? 43 : $routeName.hashCode());
      Object $predicates = this.getPredicates();
      result = result * 59 + ($predicates == null ? 43 : $predicates.hashCode());
      Object $filters = this.getFilters();
      result = result * 59 + ($filters == null ? 43 : $filters.hashCode());
      Object $uri = this.getUri();
      result = result * 59 + ($uri == null ? 43 : $uri.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      return result;
   }
}
