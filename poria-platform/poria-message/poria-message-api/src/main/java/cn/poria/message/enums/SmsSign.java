package cn.poria.message.enums;

public enum SmsSign  {

    DEFAULT(0, "【宝石花医疗】")
    ;

    private Integer value = 0;
    private String description = "";

    public String getDescription() {
        return description;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    private SmsSign(Integer _value, String _desc) {
        this.description = _desc;
        this.value = _value;
    }

    public static SmsSign fromValue(int value) {
        for (SmsSign type : SmsSign.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
