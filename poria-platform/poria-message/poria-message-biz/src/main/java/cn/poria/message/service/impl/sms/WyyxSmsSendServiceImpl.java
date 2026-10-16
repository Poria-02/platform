package cn.poria.message.service.impl.sms;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.core.util.TemplateUtil;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.SmsService;
import cn.poria.message.util.CheckSumBuilder;
import cn.poria.message.vo.request.SmsSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("wyyxSmsSendService")
@ConditionalOnProperty("message.notes.appKey")
public class WyyxSmsSendServiceImpl implements SmsService {

    @Value("${message.ntes.url}")
    private String url;

    @Value("${message.ntes.appKey}")
    private String appKey;

    @Value("${message.ntes.appSecret}")
    private String appSecret;

    @Value("${message.ntes.nonce}")
    private String nonce;

    @Value("${message.ntes.businessId}")
    private String businessId;

    @Override
    public R<String> sendSms(SmsSend smsSend, String content) {
        String[] mobiles = smsSend.getPhones().split(",");
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);
        Map<String, String> params = new HashMap<>();
        params.put("businessid", businessId);
        params.put("mobiles", quoteArray(Arrays.toString(mobiles)));
        params.put("content", TemplateUtil.process(content, smsSend.getParam()));
        log.info("网易云信短信请求参数：{} {} {} {} {} {}", url, appKey, nonce, curTime, checkSum, params);
        try (HttpResponse response = HttpUtil.createPost(url)
                .header("AppKey", appKey)
                .header("Nonce", nonce)
                .header("CurTime", curTime)
                .header("CheckSum", checkSum)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .formStr(params).execute()) {
            String result = response.body();
            log.info("网易云信短信返回：{}", result);
            return R.ok(result);
        }
    }

    private static String quoteArray(String arrayAsString) {
        return arrayAsString.replace("[", "[\"")
                .replace("]", "\"]")
                .replace(", ", "\", \"");
    }

    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.NTES.getCode().equals(platform);
    }
}
