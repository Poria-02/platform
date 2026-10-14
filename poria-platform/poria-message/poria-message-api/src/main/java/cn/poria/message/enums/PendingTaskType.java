package cn.poria.message.enums;

/**
 * 待办事项类型
 *
 */
public enum PendingTaskType {

    /*
     * 枚举顺序不能修改
     */
    NULL(0,"占位"),

    STATION_LETTER(99,"站内信"),

    ANNOUNCEMENT(98,"公示公告"),

    ;

    PendingTaskType(int code){
        this.code = code;
    }

    PendingTaskType(int code, String value) {
        this.code = code;
        this.value = value;
    }

    private int code = 0;
    private String value = "";

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
