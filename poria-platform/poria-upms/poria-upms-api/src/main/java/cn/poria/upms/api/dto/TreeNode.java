package cn.poria.upms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(
   description = "树形节点"
)
public class TreeNode {
   @Schema(
      description = "当前节点id"
   )
   protected Long id;
   @Schema(
      description = "父节点id"
   )
   protected Long parentId;
   @Schema(
      description = "子节点列表"
   )
   protected List<TreeNode> children = new ArrayList();

   public void add(TreeNode node) {
      this.children.add(node);
   }

   public Long getId() {
      return this.id;
   }

   public Long getParentId() {
      return this.parentId;
   }

   public List<TreeNode> getChildren() {
      return this.children;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setParentId(final Long parentId) {
      this.parentId = parentId;
   }

   public void setChildren(final List<TreeNode> children) {
      this.children = children;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TreeNode)) {
         return false;
      } else {
         TreeNode other = (TreeNode)o;
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

            Object this$parentId = this.getParentId();
            Object other$parentId = other.getParentId();
            if (this$parentId == null) {
               if (other$parentId != null) {
                  return false;
               }
            } else if (!this$parentId.equals(other$parentId)) {
               return false;
            }

            Object this$children = this.getChildren();
            Object other$children = other.getChildren();
            if (this$children == null) {
               if (other$children != null) {
                  return false;
               }
            } else if (!this$children.equals(other$children)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof TreeNode;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $parentId = this.getParentId();
      result = result * 59 + ($parentId == null ? 43 : $parentId.hashCode());
      Object $children = this.getChildren();
      result = result * 59 + ($children == null ? 43 : $children.hashCode());
      return result;
   }

   public String toString() {
      Long var10000 = this.getId();
      return "TreeNode(id=" + var10000 + ", parentId=" + this.getParentId() + ", children=" + this.getChildren() + ")";
   }
}
