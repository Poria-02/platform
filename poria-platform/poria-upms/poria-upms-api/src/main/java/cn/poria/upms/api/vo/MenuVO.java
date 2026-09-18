package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(
   description = "菜单展示对象"
)
public class MenuVO implements Serializable {
   private static final long serialVersionUID = 1L;
   @Schema(
      description = "菜单id"
   )
   private Long menuId;
   @Schema(
      description = "菜单名称"
   )
   private String name;
   @Schema(
      description = "菜单权限标识"
   )
   private String permission;
   @Schema(
      description = "父菜单id"
   )
   private Long parentId;
   @Schema(
      description = "图标"
   )
   private String icon;
   @Schema(
      description = "前端路由标识路径"
   )
   private String path;
   @Schema(
      description = "排序值"
   )
   private Integer sort;
   @Schema(
      description = "菜单类型,0:菜单 1:按钮"
   )
   private String type;
   @Schema(
      description = "路由缓冲"
   )
   private String keepAlive;
   @Schema(
      description = "创建时间"
   )
   private LocalDateTime createTime;
   @Schema(
      description = "更新时间"
   )
   private LocalDateTime updateTime;
   @Schema(
      description = "删除标记,1:已删除,0:正常"
   )
   private String delFlag;
   @Schema(
      description = "所属平台"
   )
   private String platform;

   public int hashCode() {
      return this.menuId.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj instanceof MenuVO) {
         Long targetMenuId = ((MenuVO)obj).getMenuId();
         return this.menuId.equals(targetMenuId);
      } else {
         return super.equals(obj);
      }
   }

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
      return "MenuVO(menuId=" + var10000 + ", name=" + this.getName() + ", permission=" + this.getPermission() + ", parentId=" + this.getParentId() + ", icon=" + this.getIcon() + ", path=" + this.getPath() + ", sort=" + this.getSort() + ", type=" + this.getType() + ", keepAlive=" + this.getKeepAlive() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ", platform=" + this.getPlatform() + ")";
   }
}
