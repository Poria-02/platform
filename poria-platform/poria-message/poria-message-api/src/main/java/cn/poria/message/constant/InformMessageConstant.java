package cn.poria.message.constant;

import cn.poria.message.enums.TemplateType;

public interface InformMessageConstant {

    /**
     * 通知方式与模板类型保持一致
     * 1 短信  4 小程序通知
     */
    Integer INFORM_WX = Integer.valueOf(TemplateType.MA.getCode());
    Integer INFORM_SMS = Integer.valueOf(TemplateType.SMS.getCode());

    /**
     * 视频录制缓存key
     */
    public final static String TRTC_VIDEO = "TRTC_VIDEO:";
}
