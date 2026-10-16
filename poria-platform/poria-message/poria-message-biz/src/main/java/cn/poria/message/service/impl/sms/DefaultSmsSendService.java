package cn.poria.message.service.impl.sms;

import cn.poria.common.core.util.R;
import cn.poria.message.enums.SmsPlatformEnum;
import cn.poria.message.service.SmsService;
import cn.poria.message.vo.request.SmsSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhangchunlei
 * @date 2021年11月18日 2:38 下午
 */
@Slf4j
@Service("defaultSmsSendService")
public class DefaultSmsSendService implements SmsService {

    @Override
    public R<String> sendSms(SmsSend smsSend, String content) {
        return R.ok("发送成功");
    }

    @Override
    public boolean supports(String platform) {
        return SmsPlatformEnum.DEFAULT.getCode().equals(platform);
    }
}
