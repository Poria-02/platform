package cn.poria.message.service.impl.sms;

import cn.hutool.json.JSONUtil;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.MsgSmsLogService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.SmsService;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchunlei
 * @date 2021年11月18日 2:38 下午
 */
@Slf4j
@Service("defaultSmsSendService")
public class DefaultSmsSendService implements SmsService {


    @Resource
    private MsgTemplateService templateService;

    @Resource
    private MsgSmsLogService smsLogService;

    @Override
    public void sendSms(SmsSend smsSend, TemplateVo template) {

        String content = templateService.getContentByKey(smsSend.getKey(),smsSend.getParam());

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
            msgSmsLog.setResult("发送成功");
            msgSmsLog.setParams(JSONUtil.toJsonStr(smsSend.getParam()));
            msgSmsLog.setCreateTime(new Date());
            msgSmsLog.setStatus(1);
            logs.add(msgSmsLog);
        }
        smsLogService.saveBatch(logs);

    }

    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.DEFAULT.getCode().equals(platform);
    }
}
