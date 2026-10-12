package cn.poria.message.constant;

public interface InformMessageConstant {

    /**
     * 发送信息的三种方式
     * 1 小程序通知  2 短信  3 电话
     */
    Integer INFORM_WX = 1;
    Integer INFORM_SMS = 2;
    Integer INFORM_PHONE = 3;

    /**
     * 视频录制缓存key
     */
    public final static String TRTC_VIDEO = "TRTC_VIDEO:";
}
