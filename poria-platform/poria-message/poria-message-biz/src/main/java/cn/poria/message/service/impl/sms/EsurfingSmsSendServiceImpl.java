package cn.poria.message.service.impl.sms;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.MsgSmsLogService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.SmsService;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 天翼云信短信发送平台
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

  @Autowired
  private MsgTemplateService templateService;

  @Autowired
  private MsgSmsLogService smsLogService;


  @Override
  public void sendSms(SmsSend smsSend, TemplateVo template) {
    String[] mobiles = smsSend.getPhones().split(",");
    List<MsgSmsLog> logs = new ArrayList<>();
    for (String mobile : mobiles) {
      String content = templateService.getContentByKey(smsSend.getKey(),smsSend.getParam());
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
      log.info("天翼云信短信平台发送路径:{} 签名参数:{} 发送参数:{}", sendUrl, appKey + timeInMillis + mobile + content + appSecret, JSONUtil.toJsonStr(esurfingVo));
      String result = HttpUtil.post(sendUrl, JSONUtil.toJsonStr(esurfingVo));
      log.info("天翼云信短信发送返回信息:{}",result);
      //保存发送记录
      MsgSmsLog msgSmsLog = new MsgSmsLog();
      msgSmsLog.setContent(content);
      msgSmsLog.setPhone(mobile);
      msgSmsLog.setTemplateKey(smsSend.getKey());
      msgSmsLog.setPlatform(template.getPlatform());
      msgSmsLog.setResult(result);
      msgSmsLog.setParams(JSONUtil.toJsonStr(smsSend.getParam()));
      msgSmsLog.setCreateTime(new Date());
      if("1".equals(JSONUtil.parseObj(result).get("status"))){
        msgSmsLog.setStatus(1);
      }else {
        msgSmsLog.setStatus(2);
      }
      logs.add(msgSmsLog);
    }
    smsLogService.saveBatch(logs);
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
     *  定时时间
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
