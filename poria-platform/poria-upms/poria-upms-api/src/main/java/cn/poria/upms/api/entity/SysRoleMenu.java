package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "角色菜单")
public class SysRoleMenu extends Model<SysRoleMenu> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色id")
    private Integer roleId;

    @Schema(description = "菜单id")
    private Integer menuId;

    public Integer getRoleId() {
        return this.roleId;
    }

    public Integer getMenuId() {
        return this.menuId;
    }

    public void setRoleId(final Integer roleId) {
        this.roleId = roleId;
    }

    public void setMenuId(final Integer menuId) {
        this.menuId = menuId;
    }

    public String toString() {
        Integer var10000 = this.getRoleId();
        return "SysRoleMenu(roleId=" + var10000 + ", menuId=" + this.getMenuId() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SysRoleMenu)) {
            return false;
        } else {
            SysRoleMenu other = (SysRoleMenu) o;
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

                Object this$menuId = this.getMenuId();
                Object other$menuId = other.getMenuId();
                if (this$menuId == null) {
                    if (other$menuId != null) {
                        return false;
                    }
                } else if (!this$menuId.equals(other$menuId)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SysRoleMenu;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Object $roleId = this.getRoleId();
        result = result * 59 + ($roleId == null ? 43 : $roleId.hashCode());
        Object $menuId = this.getMenuId();
        result = result * 59 + ($menuId == null ? 43 : $menuId.hashCode());
        return result;
    }
}
