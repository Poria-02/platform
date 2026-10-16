package cn.poria.message.service.impl.sms;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.poria.common.core.util.R;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.SmsService;
import cn.poria.message.vo.request.SmsSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author zhangchunlei
 * @date 2021年07月13日 4:31 下午
 */
@Slf4j
@Service("masSmsSendServiceImpl")
@ConditionalOnProperty("message.mas.url")
public class MasSmsSendServiceImpl implements SmsService {

    //企业名称
    @Value("${message.mas.ecName}")
    private String ecName;

    @Value("${message.mas.apId}")
    private String apId;

    @Value("${message.mas.sign}")
    private String sign;

    @Value("${message.mas.secretKey}")
    private String secretKey;

    @Value("${message.mas.url}")
    private String url;

    @Override
    public R<String> sendSms(SmsSend smsSend, String content) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.putIfAbsent("ecName",ecName);
        jsonParam.putIfAbsent("apId",apId);
        jsonParam.putIfAbsent("mobiles",smsSend.getPhones());
        jsonParam.putIfAbsent("content",content);
        jsonParam.putIfAbsent("sign",sign);
        jsonParam.putIfAbsent("addSerial","");
        jsonParam.putIfAbsent("mac", MD5.create().digestHex(String.join("",ecName,apId,secretKey,smsSend.getPhones(),content,sign,"")).toLowerCase());
        String strParam = Base64.encode(jsonParam.toString(),"UTF-8");
        log.info("短信请求参数:{}",strParam);

        String result = HttpUtil.post(url,strParam);
        log.info("短信响应结果:{}",result);

        return R.ok(result);
    }

    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.MAS.getCode().equals(platform);
    }
}
