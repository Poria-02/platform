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
    SIGN_FAMILY_STAFF(1,"签约家医入口"),
    STAFF_IM_MSG(2,"医生图文消息通知"),
    IM_MSG_SUMMARY(3,"咨询小结"),
    LINE_PRESCRIPTION(4,"电子处方"),
    RESERVE_CONSULTATION_MSG(5,"会诊预约消息通知"),
    GREEN_CHANNEL(6,"就医协助消息通知"),
    RESERVE_REGISTER_MSG(7,"预约挂号就诊通知"),
    STOP_MEDICAL_MSG(8,"停诊提醒"),
    LINE_UP_MSG(9,"排队叫号通知"),
    OUT_HOSPITAL_PAY_MSG(10,"门诊缴费通知"),
    CHECK_REPORT_MSG(11,"检查报告通知"),
    EXAMINE_REPORT_MSG(12,"检验报告通知"),
    IN_HOSPITAL_PAY_MSG(13,"住院缴费通知"),
    IN_HOSPITAL_REPORT_MSG(14,"住院报告通知"),
    KEEP(15,"保留"),
    MEDICAL_RESERVATION_MSG(16,"体检预约提醒"),
    MEDICAL_REPORT_MSG(17,"体检报告通知"),
    INTERVENE_TASK_MSG(18,"干预任务提醒"),

    BBS_JOIN_MSG(19,"圈子加入申请"),
    BBS_INTEVE_MSG(20,"圈子邀请消息"),
    CHRONIC_GROUP_NOTIFY(21,"慢病入组提醒"),
    CHRONIC_STAFF_SUMMARY(22,"慢病医护管理小结填写通知"),
    CHRONIC_STAFF_REFERRAL(23,"转诊入组通知"),
    CHRONIC_STAFF_SIGN(24,"慢病签约提醒通知"),
    //25--签约 26--慢病自治小组
    CHRONIC_PATIENT_WARNING_SIGN(25,"慢病高危预警"),
    CHRONIC_PATIENT_WARNING_AUT(26,"慢病自治小组高危预警"),
    INFECTIOUS_SUSPECTED_SEND_VISIT(27,"疑似病例就诊引导"),


    STATION_LETTER(99,"站内信"),

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
