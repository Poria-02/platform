package cn.poria.message.service.impl.sms;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.IdUtils;
import cn.poria.common.core.util.TemplateUtil;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.MsgSmsLogService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.SmsService;
import cn.poria.message.util.CheckSumBuilder;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("wyyxSmsSendService")
@ConditionalOnProperty("message.ntes.appKey")
public class WyyxSmsSendServiceImpl implements SmsService {

    @Resource
    private MsgTemplateService templateService;

    @Resource
    private MsgSmsLogService logService;

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
    public void sendSms(SmsSend smsSend, TemplateVo template) {
        String[] mobiles = smsSend.getPhones().split(",");
        List<MsgSmsLog> logs = new ArrayList<>();
        String content = templateService.getContentByKey(smsSend.getKey(),smsSend.getParam());
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);
        Map<String, String> params = new HashMap<>();
        params.put("businessid",businessId);
        params.put("mobiles", quoteArray(Arrays.toString(mobiles)));
        params.put("content", TemplateUtil.process(content,smsSend.getParam()));
        log.info("网易云信短信请求参数：{} {} {} {} {} {}", url, appKey, nonce, curTime, checkSum, params);
        String result = null;
        try {
            HttpResponse response = HttpUtil.createPost(url)
                    .header("AppKey", appKey)
                    .header("Nonce", nonce)
                    .header("CurTime", curTime)
                    .header("CheckSum", checkSum)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .formStr(params).execute();
            result= response.body();
            log.info("网易云信短信返回：{}", result);
        }catch (Exception e){
            log.error("网易云信短信发送失败：",e);
        }
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
        logService.saveBatch(logs);
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
