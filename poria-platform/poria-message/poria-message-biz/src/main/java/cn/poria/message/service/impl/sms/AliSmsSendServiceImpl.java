package cn.poria.message.service.impl.sms;

import cn.hutool.json.JSONUtil;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.MsgSmsLogService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.SmsService;
import cn.poria.message.util.AliSMSUtil;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Resource
    private MsgTemplateService msgTemplateService;

    @Resource
    private MsgSmsLogService smsLogService;


    /**
     * 短信发送
     *
     * @param smsSend
     */
    @Override
    public void sendSms(SmsSend smsSend, TemplateVo template) {

        String content = msgTemplateService.getContentByKey(smsSend.getKey(), smsSend.getParam());
        Client client;
        String result;
        try {
            client = AliSMSUtil
                .createClient(apId, secretKey);

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(sign)
                //模板内容中存放 对应的 阿里云模板名称
                .setTemplateCode(content)
                .setPhoneNumbers(smsSend.getPhones())
                .setTemplateParam(JSONUtil.toJsonStr(smsSend.getParam()));
            log.info("短信请求参数:{}",content);
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            result = AliSMSUtil.responseToString(sendSmsResponse.getBody());
            log.info("短信响应结果:{}", result);

        } catch (Exception e) {
            log.error("短信发送失败:",e);
            result = "发送短信失败!!";
        }

        //保存发送记录
        String[] mobiles = smsSend.getPhones().split(",");
        List<MsgSmsLog> logs = new ArrayList<>();
        for (String mobile : mobiles) {
            //保存发送记录
            MsgSmsLog msgSmsLog = new MsgSmsLog();
            msgSmsLog.setContent(content);
            msgSmsLog.setPhone(mobile);
            msgSmsLog.setTemplateKey(smsSend.getKey());
            msgSmsLog.setPlatform(template.getPlatform());
            msgSmsLog.setResult(result);
            msgSmsLog.setParams(JSONUtil.toJsonStr(smsSend.getParam()));
            msgSmsLog.setCreateTime(new Date());
            msgSmsLog.setStatus(1);
            logs.add(msgSmsLog);
        }
        smsLogService.saveBatch(logs);

    }

    /**
     * 根据短信平台判断是否支持
     *
     * @param platform
     * @return
     */
    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.ALI.getCode().equals(platform);
    }
}
