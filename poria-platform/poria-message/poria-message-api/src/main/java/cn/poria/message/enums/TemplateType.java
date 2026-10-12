package cn.poria.message.enums;

/**
 * 模板类型
 */
public enum TemplateType {

    SMS("1","短信消息"),
    VOICE("2","语音消息模版"),
    PUSH("3","推送消息模版"),
    STATION("4","站内信模版"),
    WX("5","微信消息模板"),
    DINGDING("6","钉钉消息模板"),
    QYWX("7","企业微信消息模板"),

    MA("9","小程序订阅消息"),

    ;


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
