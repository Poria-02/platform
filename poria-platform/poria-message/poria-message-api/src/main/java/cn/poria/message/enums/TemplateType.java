package cn.poria.message.enums;

/**
 * 模板类型
 */
public enum TemplateType {

    SMS("1","短信消息"),
    STATION("2","站内信消息"),
    WX("3","微信公众号消息"),
    MA("4","小程序订阅消息");


    TemplateType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
