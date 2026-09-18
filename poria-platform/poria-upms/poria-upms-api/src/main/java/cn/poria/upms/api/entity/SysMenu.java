package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "菜单")
public class SysMenu extends Model<SysMenu> {
   private static final long serialVersionUID = 1L;

   @TableId(value = "menu_id", type = IdType.AUTO)
   @Schema(description = "菜单id")
   private Long menuId;

   @Schema(description = "菜单名称")
   @NotBlank(message = "菜单名称不能为空")
   private String name;

   @Schema(description = "菜单权限标识")
   private String permission;

   @Schema(description = "菜单父id")
   @NotNull(message = "菜单父ID不能为空")
   private Long parentId;

   @Schema(description = "菜单图标")
   private String icon;

   @Schema(description = "前端路由标识路径")
   private String path;

   @Schema(description = "排序值")
   private Integer sort;

   @Schema(description = "菜单类型,0:菜单 1:按钮")
   @NotNull(message = "菜单类型不能为空")
   private String type;

   @Schema(description = "路由缓冲")
   private String keepAlive;

   @Schema(description = "创建时间")
   private LocalDateTime createTime;

   @Schema(description = "更新时间")
   private LocalDateTime updateTime;

   @TableLogic
   @Schema(description = "删除标记,1:已删除,0:正常")
   private String delFlag;

   @Schema(description = "所属平台")
   private String platform;

   public Long getMenuId() {
      return this.menuId;
   }

   public String getName() {
      return this.name;
   }

   public String getPermission() {
      return this.permission;
   }

   public Long getParentId() {
      return this.parentId;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getPath() {
      return this.path;
   }

   public Integer getSort() {
      return this.sort;
   }

   public String getType() {
      return this.type;
   }

   public String getKeepAlive() {
      return this.keepAlive;
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

   public String getPlatform() {
      return this.platform;
   }

   public void setMenuId(final Long menuId) {
      this.menuId = menuId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setPermission(final String permission) {
      this.permission = permission;
   }

   public void setParentId(final Long parentId) {
      this.parentId = parentId;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setPath(final String path) {
      this.path = path;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setKeepAlive(final String keepAlive) {
      this.keepAlive = keepAlive;
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

   public void setPlatform(final String platform) {
      this.platform = platform;
   }

   public String toString() {
      Long var10000 = this.getMenuId();
      return "SysMenu(menuId=" + var10000 + ", name=" + this.getName() + ", permission=" + this.getPermission() + ", parentId=" + this.getParentId() + ", icon=" + this.getIcon() + ", path=" + this.getPath() + ", sort=" + this.getSort() + ", type=" + this.getType() + ", keepAlive=" + this.getKeepAlive() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ", platform=" + this.getPlatform() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SysMenu)) {
         return false;
      } else {
         SysMenu other = (SysMenu)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$menuId = this.getMenuId();
            Object other$menuId = other.getMenuId();
            if (this$menuId == null) {
               if (other$menuId != null) {
                  return false;
               }
            } else if (!this$menuId.equals(other$menuId)) {
               return false;
            }

            Object this$parentId = this.getParentId();
            Object other$parentId = other.getParentId();
            if (this$parentId == null) {
               if (other$parentId != null) {
                  return false;
               }
            } else if (!this$parentId.equals(other$parentId)) {
               return false;
            }

            Object this$sort = this.getSort();
            Object other$sort = other.getSort();
            if (this$sort == null) {
               if (other$sort != null) {
                  return false;
               }
            } else if (!this$sort.equals(other$sort)) {
               return false;
            }

            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
               return false;
            }

            Object this$permission = this.getPermission();
            Object other$permission = other.getPermission();
            if (this$permission == null) {
               if (other$permission != null) {
                  return false;
               }
            } else if (!this$permission.equals(other$permission)) {
               return false;
            }

            Object this$icon = this.getIcon();
            Object other$icon = other.getIcon();
            if (this$icon == null) {
               if (other$icon != null) {
                  return false;
               }
            } else if (!this$icon.equals(other$icon)) {
               return false;
            }

            Object this$path = this.getPath();
            Object other$path = other.getPath();
            if (this$path == null) {
               if (other$path != null) {
                  return false;
               }
            } else if (!this$path.equals(other$path)) {
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

            Object this$keepAlive = this.getKeepAlive();
            Object other$keepAlive = other.getKeepAlive();
            if (this$keepAlive == null) {
               if (other$keepAlive != null) {
                  return false;
               }
            } else if (!this$keepAlive.equals(other$keepAlive)) {
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

            Object this$platform = this.getPlatform();
            Object other$platform = other.getPlatform();
            if (this$platform == null) {
               if (other$platform != null) {
                  return false;
               }
            } else if (!this$platform.equals(other$platform)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof SysMenu;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $menuId = this.getMenuId();
      result = result * 59 + ($menuId == null ? 43 : $menuId.hashCode());
      Object $parentId = this.getParentId();
      result = result * 59 + ($parentId == null ? 43 : $parentId.hashCode());
      Object $sort = this.getSort();
      result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $permission = this.getPermission();
      result = result * 59 + ($permission == null ? 43 : $permission.hashCode());
      Object $icon = this.getIcon();
      result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
      Object $path = this.getPath();
      result = result * 59 + ($path == null ? 43 : $path.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $keepAlive = this.getKeepAlive();
      result = result * 59 + ($keepAlive == null ? 43 : $keepAlive.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
      Object $delFlag = this.getDelFlag();
      result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
      Object $platform = this.getPlatform();
      result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
      return result;
   }
}
