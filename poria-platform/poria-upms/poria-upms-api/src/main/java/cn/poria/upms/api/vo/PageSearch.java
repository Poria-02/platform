package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class PageSearch {
   @Schema(
      description = "第N页"
   )
   private @NotNull Integer current = 1;
   @Schema(
      description = "每页N条"
   )
   private @NotNull Integer size = 10;

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PageSearch)) {
         return false;
      } else {
         PageSearch other = (PageSearch)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$current = this.getCurrent();
            Object other$current = other.getCurrent();
            if (this$current == null) {
               if (other$current != null) {
                  return false;
               }
            } else if (!this$current.equals(other$current)) {
               return false;
            }

            Object this$size = this.getSize();
            Object other$size = other.getSize();
            if (this$size == null) {
               if (other$size != null) {
                  return false;
               }
            } else if (!this$size.equals(other$size)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof PageSearch;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $current = this.getCurrent();
      result = result * 59 + ($current == null ? 43 : $current.hashCode());
      Object $size = this.getSize();
      result = result * 59 + ($size == null ? 43 : $size.hashCode());
      return result;
   }

   public String toString() {
      Integer var10000 = this.getCurrent();
      return "PageSearch(current=" + var10000 + ", size=" + this.getSize() + ")";
   }
}
