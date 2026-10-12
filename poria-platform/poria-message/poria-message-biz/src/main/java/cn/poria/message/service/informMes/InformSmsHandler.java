package cn.poria.message.service.informMes;

import cn.hutool.core.collection.CollectionUtil;
import cn.poria.message.service.SmsServiceManager;
import cn.poria.message.vo.request.InformMesModel;
import cn.poria.message.vo.request.SmsSend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InformSmsHandler implements InformMessageHandler {

    private final SmsServiceManager smsServiceManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerInformMes(InformMesModel model) {
        log.info("发送短信。。。");

        SmsSend sms = new SmsSend();
        sms.setKey(model.getMsgTemplateKey());
        sms.setPhones(model.getPatientPhone());
        if (CollectionUtil.isNotEmpty(model.getParams())) {
            sms.setParam(model.getParams());
        }

        smsServiceManager.sendSms(sms);

        log.info("发送短信 {}", model.getPatientPhone());
    }
}
