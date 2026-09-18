package cn.poria.upms.api.enums;

public enum DeptTypeEnums {
   ORGANIZATION(1, "机构"),
   DEPART(2, "科室");

   private Integer code;
   private String value;

   public Integer getCode() {
      return this.code;
   }

   public void setCode(Integer code) {
      this.code = code;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   private DeptTypeEnums(Integer code, String value) {
      this.code = code;
      this.value = value;
   }

   // $FF: synthetic method
   private static DeptTypeEnums[] $values() {
      return new DeptTypeEnums[]{ORGANIZATION, DEPART};
   }
}
