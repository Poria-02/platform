package cn.poria.message.enums;

public enum SmsPlatformEnum {

    ALI("ali","阿里云平台"),
    IH("ih","互联网医院"),
    MAS("mas","移动云MAS"),
    DEFAULT("default","测试默认"),

    LT("lt","联通"),

    ESURFING("esurfing","天翼云信"),

    NTES("ntes","网易云信"),

    ;

    private String code;

    private String value;

    SmsPlatformEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
