package cn.poria.message.service.impl.sms;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.SmsService;
import cn.poria.message.vo.request.SmsSend;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * 天翼云信短信发送平台
 *
 * @author wuning
 */
@Slf4j
@Service("esurfingSmsSendServiceImpl")
@ConditionalOnProperty("message.esurfing.plat.url")
public class EsurfingSmsSendServiceImpl implements SmsService {

    @Value("${message.esurfing.plat.url}")
    private String url;

    @Value("${message.esurfing.plat.appKey}")
    private String appKey;

    @Value("${message.esurfing.plat.appSecret}")
    private String appSecret;

    @Value("${message.esurfing.plat.reportUrl}")
    private String reportUrl;

    @Value("${message.esurfing.plat.moUrl}")
    private String moUrl;

    @Override
    public R<String> sendSms(SmsSend smsSend, String content) {
        String mobile = smsSend.getPhones();
        EsurfingVo esurfingVo = new EsurfingVo();
        esurfingVo.setAppKey(appKey);
        String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());
        esurfingVo.setTimestamp(timeInMillis);
        esurfingVo.setMobile(mobile);
        esurfingVo.setContent(content);
        esurfingVo.setSendTime("");
        esurfingVo.setSpNumber("");
        esurfingVo.setReportUrl("");
        esurfingVo.setMoUrl("");
        esurfingVo.setAttach("");
        String sign = SecureUtil.md5(appKey + timeInMillis + mobile + content + appSecret);
        esurfingVo.setSign(sign);

        String sendUrl = url + "/api/sms/air/send";
        log.info("天翼云信短信平台发送路径:{} 发送参数:{}", sendUrl, JSONUtil.toJsonStr(esurfingVo));
        String result = HttpUtil.post(sendUrl, JSONUtil.toJsonStr(esurfingVo));
        log.info("天翼云信短信发送返回信息:{}", result);
    try {
      return "1".equals(JSONUtil.parseObj(result).getStr("status"))
          ? R.ok(result) : R.failed(result, "天翼云信短信发送失败");
    } catch (JSONException e) {
      return R.failed(result, "天翼云信短信返回格式异常");
    }
    }

    @Override
    public boolean supportsBatch() {
        return false;
    }

    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.ESURFING.getCode().equals(platform);
    }

    @Data
    private class EsurfingVo {

        /**
         * 账户名
         */
        private String appKey;

        /**
         * 时间戳
         */
        private String timestamp;

        /**
         * 用户号码
         */
        private String mobile;

        /**
         * 内容长度
         */
        private String content;

        /**
         * 定时时间
         */
        private String sendTime;

        /**
         * 扩展号码
         */
        private String spNumber;

        /**
         * 数字签名
         */
        private String sign;

        /**
         * 报告回调地址
         */
        private String reportUrl;

        /**
         * 上行地址
         */
        private String moUrl;

        /**
         * 附加值字段
         */
        private String attach;
    }
}
