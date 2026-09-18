package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ExportDictModel {
   @Schema(
      description = "字典类型"
   )
   private List<String> dictTypes;
   @Schema(
      description = "分级菜单code"
   )
   private List<String> dictTreeCode;

   public List<String> getDictTypes() {
      return this.dictTypes;
   }

   public List<String> getDictTreeCode() {
      return this.dictTreeCode;
   }

   public void setDictTypes(final List<String> dictTypes) {
      this.dictTypes = dictTypes;
   }

   public void setDictTreeCode(final List<String> dictTreeCode) {
      this.dictTreeCode = dictTreeCode;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ExportDictModel)) {
         return false;
      } else {
         ExportDictModel other = (ExportDictModel)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$dictTypes = this.getDictTypes();
            Object other$dictTypes = other.getDictTypes();
            if (this$dictTypes == null) {
               if (other$dictTypes != null) {
                  return false;
               }
            } else if (!this$dictTypes.equals(other$dictTypes)) {
               return false;
            }

            Object this$dictTreeCode = this.getDictTreeCode();
            Object other$dictTreeCode = other.getDictTreeCode();
            if (this$dictTreeCode == null) {
               if (other$dictTreeCode != null) {
                  return false;
               }
            } else if (!this$dictTreeCode.equals(other$dictTreeCode)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof ExportDictModel;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $dictTypes = this.getDictTypes();
      result = result * 59 + ($dictTypes == null ? 43 : $dictTypes.hashCode());
      Object $dictTreeCode = this.getDictTreeCode();
      result = result * 59 + ($dictTreeCode == null ? 43 : $dictTreeCode.hashCode());
      return result;
   }

   public String toString() {
      List var10000 = this.getDictTypes();
      return "ExportDictModel(dictTypes=" + var10000 + ", dictTreeCode=" + this.getDictTreeCode() + ")";
   }
}
