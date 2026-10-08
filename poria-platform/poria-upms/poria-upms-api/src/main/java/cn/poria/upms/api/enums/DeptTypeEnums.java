package cn.poria.upms.api.enums;

import lombok.Getter;

@Getter
public enum DeptTypeEnums {
    ORGANIZATION(1, "机构"),
    DEPART(2, "科室");

    private final Integer code;

    private final String value;

    DeptTypeEnums(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

   // $FF: synthetic method
    private static DeptTypeEnums[] $values() {
        return new DeptTypeEnums[]{ORGANIZATION, DEPART};
    }
}
