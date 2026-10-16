package cn.poria.message.service.impl.sms;

import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.SmsService;
import cn.poria.message.util.AliSMSUtil;
import cn.poria.message.vo.request.SmsSend;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 阿里短信发送
 */
@Slf4j
@Service("aliSmsSendService")
@ConditionalOnProperty("message.alis.apId")
public class AliSmsSendServiceImpl implements SmsService {

    @Value("${message.alis.apId}")
    private String apId;

    @Value("${message.alis.sign}")
    private String sign;

    @Value("${message.alis.secretKey}")
    private String secretKey;

    /**
     * 短信发送
     *
     * @param smsSend 接收手机号及短信参数
     * @param content 阿里云短信模板编码
     * @return 平台原始响应
     * @throws Exception 通道请求异常
     */
    @Override
    public R<String> sendSms(SmsSend smsSend, String content) throws Exception {
        Client client = AliSMSUtil.createClient(apId, secretKey);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(sign)
                //模板内容中存放对应的阿里云模板编码
                .setTemplateCode(content)
                .setPhoneNumbers(smsSend.getPhones())
                .setTemplateParam(JSONUtil.toJsonStr(smsSend.getParam()));
        log.info("短信请求参数:{}", content);
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        String result = AliSMSUtil.responseToString(sendSmsResponse.getBody());
        log.info("短信响应结果:{}", result);
        return R.ok(result);
    }

    /**
     * 根据短信平台判断是否支持
     *
     * @param platform 短信平台标识
     * @return 是否支持该平台
     */
    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.ALI.getCode().equals(platform);
    }
}
