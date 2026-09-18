package cn.poria.upms.api.dto;

import cn.poria.upms.api.vo.MenuVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "菜单树"
)
public class MenuTree extends TreeNode implements Serializable {
   @Schema(
      description = "菜单图标"
   )
   private String icon;
   @Schema(
      description = "菜单名称"
   )
   private String name;
   private boolean spread = false;
   @Schema(
      description = "前端路由标识路径"
   )
   private String path;
   @Schema(
      description = "路由缓冲"
   )
   private String keepAlive;
   @Schema(
      description = "权限编码"
   )
   private String permission;
   @Schema(
      description = "菜单类型,0:菜单 1:按钮"
   )
   private String type;
   @Schema(
      description = "菜单标签"
   )
   private String label;
   @Schema(
      description = "排序值"
   )
   private Integer sort;
   private Boolean hasChildren;
   @Schema(
      description = "所属平台"
   )
   private String platform;

   public MenuTree() {
   }

   public MenuTree(long id, String name, long parentId) {
      this.id = id;
      this.name = name;
      this.label = name;
      this.parentId = parentId;
   }

   public MenuTree(long id, String name, MenuTree parent) {
      this.id = id;
      this.name = name;
      this.label = name;
      this.parentId = parent.getId();
   }

   public MenuTree(MenuVO menuVo) {
      this.id = menuVo.getMenuId();
      this.parentId = menuVo.getParentId();
      this.icon = menuVo.getIcon();
      this.name = menuVo.getName();
      this.path = menuVo.getPath();
      this.type = menuVo.getType();
      this.permission = menuVo.getPermission();
      this.label = menuVo.getName();
      this.sort = menuVo.getSort();
      this.keepAlive = menuVo.getKeepAlive();
      this.platform = menuVo.getPlatform();
   }

   public String getIcon() {
      return this.icon;
   }

   public String getName() {
      return this.name;
   }

   public boolean isSpread() {
      return this.spread;
   }

   public String getPath() {
      return this.path;
   }

   public String getKeepAlive() {
      return this.keepAlive;
   }

   public String getPermission() {
      return this.permission;
   }

   public String getType() {
      return this.type;
   }

   public String getLabel() {
      return this.label;
   }

   public Integer getSort() {
      return this.sort;
   }

   public Boolean getHasChildren() {
      return this.hasChildren;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSpread(final boolean spread) {
      this.spread = spread;
   }

   public void setPath(final String path) {
      this.path = path;
   }

   public void setKeepAlive(final String keepAlive) {
      this.keepAlive = keepAlive;
   }

   public void setPermission(final String permission) {
      this.permission = permission;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setLabel(final String label) {
      this.label = label;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }

   public void setHasChildren(final Boolean hasChildren) {
      this.hasChildren = hasChildren;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }

   public String toString() {
      String var10000 = this.getIcon();
      return "MenuTree(icon=" + var10000 + ", name=" + this.getName() + ", spread=" + this.isSpread() + ", path=" + this.getPath() + ", keepAlive=" + this.getKeepAlive() + ", permission=" + this.getPermission() + ", type=" + this.getType() + ", label=" + this.getLabel() + ", sort=" + this.getSort() + ", hasChildren=" + this.getHasChildren() + ", platform=" + this.getPlatform() + ")";
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MenuTree)) {
         return false;
      } else {
         MenuTree other = (MenuTree)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else if (this.isSpread() != other.isSpread()) {
            return false;
         } else {
            Object this$sort = this.getSort();
            Object other$sort = other.getSort();
            if (this$sort == null) {
               if (other$sort != null) {
                  return false;
               }
            } else if (!this$sort.equals(other$sort)) {
               return false;
            }

            Object this$hasChildren = this.getHasChildren();
            Object other$hasChildren = other.getHasChildren();
            if (this$hasChildren == null) {
               if (other$hasChildren != null) {
                  return false;
               }
            } else if (!this$hasChildren.equals(other$hasChildren)) {
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

            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
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

            Object this$keepAlive = this.getKeepAlive();
            Object other$keepAlive = other.getKeepAlive();
            if (this$keepAlive == null) {
               if (other$keepAlive != null) {
                  return false;
               }
            } else if (!this$keepAlive.equals(other$keepAlive)) {
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

            Object this$type = this.getType();
            Object other$type = other.getType();
            if (this$type == null) {
               if (other$type != null) {
                  return false;
               }
            } else if (!this$type.equals(other$type)) {
               return false;
            }

            Object this$label = this.getLabel();
            Object other$label = other.getLabel();
            if (this$label == null) {
               if (other$label != null) {
                  return false;
               }
            } else if (!this$label.equals(other$label)) {
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
      return other instanceof MenuTree;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      result = result * 59 + (this.isSpread() ? 79 : 97);
      Object $sort = this.getSort();
      result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
      Object $hasChildren = this.getHasChildren();
      result = result * 59 + ($hasChildren == null ? 43 : $hasChildren.hashCode());
      Object $icon = this.getIcon();
      result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $path = this.getPath();
      result = result * 59 + ($path == null ? 43 : $path.hashCode());
      Object $keepAlive = this.getKeepAlive();
      result = result * 59 + ($keepAlive == null ? 43 : $keepAlive.hashCode());
      Object $permission = this.getPermission();
      result = result * 59 + ($permission == null ? 43 : $permission.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $label = this.getLabel();
      result = result * 59 + ($label == null ? 43 : $label.hashCode());
      Object $platform = this.getPlatform();
      result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
      return result;
   }
}
