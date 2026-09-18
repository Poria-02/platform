package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class TreeDictItemModel {
   private String id;
   private @NotNull String name;
   private String simpleName = "";
   private String remark = "";
   private String value = "";
   private String ext1 = "";
   private String pid = "";
   @Schema(
      description = "排序,最长6位"
   )
   private @Max(
   value = 999999L,
   message = "排序值过大"
) @Min(
   value = 0L,
   message = "排序值太小"
) Integer sort;
   private List<TreeDictItemModel> childs;
   private @NotEmpty String dictId;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getSimpleName() {
      return this.simpleName;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getValue() {
      return this.value;
   }

   public String getExt1() {
      return this.ext1;
   }

   public String getPid() {
      return this.pid;
   }

   public Integer getSort() {
      return this.sort;
   }

   public List<TreeDictItemModel> getChilds() {
      return this.childs;
   }

   public String getDictId() {
      return this.dictId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSimpleName(final String simpleName) {
      this.simpleName = simpleName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setValue(final String value) {
      this.value = value;
   }

   public void setExt1(final String ext1) {
      this.ext1 = ext1;
   }

   public void setPid(final String pid) {
      this.pid = pid;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }

   public void setChilds(final List<TreeDictItemModel> childs) {
      this.childs = childs;
   }

   public void setDictId(final String dictId) {
      this.dictId = dictId;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TreeDictItemModel)) {
         return false;
      } else {
         TreeDictItemModel other = (TreeDictItemModel)o;
         if (!other.canEqual(this)) {
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

            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null) {
               if (other$id != null) {
                  return false;
               }
            } else if (!this$id.equals(other$id)) {
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

            Object this$simpleName = this.getSimpleName();
            Object other$simpleName = other.getSimpleName();
            if (this$simpleName == null) {
               if (other$simpleName != null) {
                  return false;
               }
            } else if (!this$simpleName.equals(other$simpleName)) {
               return false;
            }

            Object this$remark = this.getRemark();
            Object other$remark = other.getRemark();
            if (this$remark == null) {
               if (other$remark != null) {
                  return false;
               }
            } else if (!this$remark.equals(other$remark)) {
               return false;
            }

            Object this$value = this.getValue();
            Object other$value = other.getValue();
            if (this$value == null) {
               if (other$value != null) {
                  return false;
               }
            } else if (!this$value.equals(other$value)) {
               return false;
            }

            Object this$ext1 = this.getExt1();
            Object other$ext1 = other.getExt1();
            if (this$ext1 == null) {
               if (other$ext1 != null) {
                  return false;
               }
            } else if (!this$ext1.equals(other$ext1)) {
               return false;
            }

            Object this$pid = this.getPid();
            Object other$pid = other.getPid();
            if (this$pid == null) {
               if (other$pid != null) {
                  return false;
               }
            } else if (!this$pid.equals(other$pid)) {
               return false;
            }

            Object this$childs = this.getChilds();
            Object other$childs = other.getChilds();
            if (this$childs == null) {
               if (other$childs != null) {
                  return false;
               }
            } else if (!this$childs.equals(other$childs)) {
               return false;
            }

            Object this$dictId = this.getDictId();
            Object other$dictId = other.getDictId();
            if (this$dictId == null) {
               if (other$dictId != null) {
                  return false;
               }
            } else if (!this$dictId.equals(other$dictId)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof TreeDictItemModel;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $sort = this.getSort();
      result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $simpleName = this.getSimpleName();
      result = result * 59 + ($simpleName == null ? 43 : $simpleName.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      Object $value = this.getValue();
      result = result * 59 + ($value == null ? 43 : $value.hashCode());
      Object $ext1 = this.getExt1();
      result = result * 59 + ($ext1 == null ? 43 : $ext1.hashCode());
      Object $pid = this.getPid();
      result = result * 59 + ($pid == null ? 43 : $pid.hashCode());
      Object $childs = this.getChilds();
      result = result * 59 + ($childs == null ? 43 : $childs.hashCode());
      Object $dictId = this.getDictId();
      result = result * 59 + ($dictId == null ? 43 : $dictId.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getId();
      return "TreeDictItemModel(id=" + var10000 + ", name=" + this.getName() + ", simpleName=" + this.getSimpleName() + ", remark=" + this.getRemark() + ", value=" + this.getValue() + ", ext1=" + this.getExt1() + ", pid=" + this.getPid() + ", sort=" + this.getSort() + ", childs=" + this.getChilds() + ", dictId=" + this.getDictId() + ")";
   }
}
