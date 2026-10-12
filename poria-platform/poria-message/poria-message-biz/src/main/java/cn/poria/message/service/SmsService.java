package cn.poria.message.service;

import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;

public interface SmsService {

    /**
     * 短信发送
     * @param smsSend
     */
    void sendSms(SmsSend smsSend, TemplateVo template);

    /**
     * 根据短信平台判断是否支持
     * @param platform
     * @return
     */
    boolean supports(String platform);

}
