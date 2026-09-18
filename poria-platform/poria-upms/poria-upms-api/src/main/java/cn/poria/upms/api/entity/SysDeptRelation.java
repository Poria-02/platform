package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "部门关系")
public class SysDeptRelation extends Model<SysDeptRelation> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "祖先节点")
    private Long ancestor;

    @Schema(description = "后代节点")
    private Long descendant;

    public Long getAncestor() {
        return this.ancestor;
    }

    public Long getDescendant() {
        return this.descendant;
    }

    public void setAncestor(final Long ancestor) {
        this.ancestor = ancestor;
    }

    public void setDescendant(final Long descendant) {
        this.descendant = descendant;
    }

    public String toString() {
        Long var10000 = this.getAncestor();
        return "SysDeptRelation(ancestor=" + var10000 + ", descendant=" + this.getDescendant() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SysDeptRelation)) {
            return false;
        } else {
            SysDeptRelation other = (SysDeptRelation) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object this$ancestor = this.getAncestor();
                Object other$ancestor = other.getAncestor();
                if (this$ancestor == null) {
                    if (other$ancestor != null) {
                        return false;
                    }
                } else if (!this$ancestor.equals(other$ancestor)) {
                    return false;
                }

                Object this$descendant = this.getDescendant();
                Object other$descendant = other.getDescendant();
                if (this$descendant == null) {
                    if (other$descendant != null) {
                        return false;
                    }
                } else if (!this$descendant.equals(other$descendant)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SysDeptRelation;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Object $ancestor = this.getAncestor();
        result = result * 59 + ($ancestor == null ? 43 : $ancestor.hashCode());
        Object $descendant = this.getDescendant();
        result = result * 59 + ($descendant == null ? 43 : $descendant.hashCode());
        return result;
    }
}
