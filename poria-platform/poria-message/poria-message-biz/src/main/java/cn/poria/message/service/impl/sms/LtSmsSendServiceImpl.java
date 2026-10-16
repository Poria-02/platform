package cn.poria.message.service.impl.sms;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.poria.common.core.util.R;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.SmsService;
import cn.poria.message.vo.request.SmsSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LtSmsSendServiceImpl implements SmsService {

    @Value("${message.lt.loginName}")
    private String loginName;
    @Value("${message.lt.password}")
    private String password;
    @Value("${message.lt.url}")
    private String url;

    @Override
    public R<String> sendSms(SmsSend smsSend, String content) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.putIfAbsent("LoginName", loginName);
        jsonParam.putIfAbsent("Pwd", password);
        jsonParam.putIfAbsent("FeeType", 2); //2 为行业套餐 3 为政务套餐
        jsonParam.putIfAbsent("Mobile", smsSend.getPhones());
        jsonParam.putIfAbsent("Content", content);

        log.info("短信请求参数:{}", jsonParam);

        String result = HttpUtil.post(url + "SendSms", jsonParam);
        log.info("发送结果:{}", result);
        return R.ok(result);
    }

    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.LT.getCode().equals(platform);
    }
}
