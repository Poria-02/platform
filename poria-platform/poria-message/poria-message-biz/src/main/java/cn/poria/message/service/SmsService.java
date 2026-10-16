package cn.poria.message.service;

import cn.poria.common.core.util.R;
import cn.poria.message.vo.request.SmsSend;

/**
 * 短信通道发送接口，发送日志由统一入口保存
 */
public interface SmsService {

    /**
     * 短信发送
     *
     * @param smsSend 接收手机号及短信参数
     * @param content 已渲染的短信内容或平台模板编码
     * @return 通道发送结果，data保留平台原始响应
     * @throws Exception 通道请求异常
     */
    R<String> sendSms(SmsSend smsSend, String content) throws Exception;

    /**
     * 是否支持一次请求发送多个手机号
     *
     * @return 是否支持批量发送
     */
    default boolean supportsBatch() {
        return true;
    }

    /**
     * 根据短信平台判断是否支持
     *
     * @param platform 短信平台标识
     * @return 是否支持该平台
     */
    boolean supports(String platform);

}
