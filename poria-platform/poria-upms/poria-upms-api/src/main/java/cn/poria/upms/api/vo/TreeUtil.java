package cn.poria.upms.api.vo;

import cn.poria.upms.api.dto.MenuTree;
import cn.poria.upms.api.dto.TreeNode;
import cn.poria.upms.api.entity.SysMenu;
import java.util.ArrayList;
import java.util.List;

public final class TreeUtil {
   public static <T extends TreeNode> List<T> build(List<T> treeNodes, Object root) {
      List<T> trees = new ArrayList();

      for(T treeNode : treeNodes) {
         if (root.equals(treeNode.getParentId())) {
            trees.add(treeNode);
         }

         for(T it : treeNodes) {
            if (it.getParentId().equals(treeNode.getId())) {
               if (treeNode.getChildren() == null) {
                  ((TreeNode)treeNode).setChildren(new ArrayList());
               }

               treeNode.add(it);
            }
         }
      }

      return trees;
   }

   public static <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes, Object root) {
      List<T> trees = new ArrayList();

      for(T treeNode : treeNodes) {
         if (root.equals(treeNode.getParentId())) {
            trees.add(findChildren(treeNode, treeNodes));
         }
      }

      return trees;
   }

   public static <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
      for(T it : treeNodes) {
         if (treeNode.getId().equals(it.getParentId())) {
            if (treeNode.getChildren() == null) {
               ((TreeNode)treeNode).setChildren(new ArrayList());
            }

            treeNode.add(findChildren(it, treeNodes));
         }
      }

      return treeNode;
   }

   public static List<MenuTree> buildTree(List<SysMenu> menus, long root) {
      List<MenuTree> trees = new ArrayList();

      for(SysMenu menu : menus) {
         MenuTree node = new MenuTree();
         node.setId(menu.getMenuId());
         node.setParentId(menu.getParentId());
         node.setName(menu.getName());
         node.setPath(menu.getPath());
         node.setPermission(menu.getPermission());
         node.setLabel(menu.getName());
         node.setIcon(menu.getIcon());
         node.setType(menu.getType());
         node.setSort(menu.getSort());
         node.setHasChildren(true);
         node.setKeepAlive(menu.getKeepAlive());
         node.setPlatform(menu.getPlatform());
         trees.add(node);
      }

      return build(trees, root);
   }

   private TreeUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
