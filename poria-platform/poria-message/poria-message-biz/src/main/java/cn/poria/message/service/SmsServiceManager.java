package cn.poria.message.service;

import cn.hutool.core.date.DateUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.util.TemplateParamUtil;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SmsServiceManager {

    @Autowired
    private MsgTemplateService templateService;

    private List<SmsService> smsServiceList;

    @Autowired
    private RedisTemplate redisTemplate;

    static final String SMS_REPEAT_LIMIT_KEY = "SMS_REPEAT_LIMIT_KEY:%s:%s";

    static final String SMS_MAX_TOTAL_LIMIT_KEY = "SMS_MAX_TOTAL_LIMIT_KEY:%s";

    static final String SMS_PHONE_MAX_LIMIT_KEY = "SMS_PHONE_MAX_LIMIT_KEY:%s:%s";

    public SmsServiceManager(List<SmsService> smsServices){
        this.smsServiceList = smsServices;
    }

    public void sendSms(SmsSend req) {

        TemplateVo template = templateService.selectByKey(req.getKey());
        Assert.notNull(template,"模版:{}编码不存在",req.getKey());
        Assert.isTrue(TemplateType.SMS.getCode().equals(template.getType()), "模板类型必须为短信");
        if(template.getStatus() ==2){
            log.info("模版{}已停用",template.getTitle());
            return;
        }
//        limit(req);

        //动态设置模板参数
        Map<String, String> param = TemplateParamUtil.settingTemplateParam(template, req.getParam());
        req.setParam(param);

        for (SmsService smsService : smsServiceList){
            if(smsService.supports(template.getPlatform())){
                smsService.sendSms(req,template);
                break;
            }
        }
    }


    /**
     * 限流操作
     * @param send
     */
    private void limit(SmsSend send){

        //每日短信发送总数限制
        maxTotalLimit();

        //同一手机号 重复短信限制
        repeatLimit(send);

        //同一手机号,当天短信数量限制
        phoneMaxNumLimit(send);



    }

    /**
     * 每日最大数量限制
     */
    private void maxTotalLimit(){

        String date = DateUtil.formatDate(new Date());
        Object object = redisTemplate.opsForValue().get(String.format(SMS_MAX_TOTAL_LIMIT_KEY,date));
        int total = object==null?0:(int)object;

        if(total  > Integer.MAX_VALUE){
            throw new ServiceException("超出当日最大发送限制");
        }

        redisTemplate.opsForValue().set(String.format(SMS_MAX_TOTAL_LIMIT_KEY,date),total+1,1,TimeUnit.DAYS);

    }

    /**
     * 相同手机号 相同模版 一分钟之内不允许重复发送短信
     * @param send
     */
    private void repeatLimit(SmsSend send){

        String key = send.getPhones().length()>15?""+send.getPhones().hashCode():send.getPhones();
        Object obj = redisTemplate.opsForValue().get(String.format(SMS_REPEAT_LIMIT_KEY,send.getKey(),key));
        if(obj != null && send.getPhones().equals(obj)){
            throw new ServiceException("重复发送,请稍后再试");
        }

        redisTemplate.opsForValue().set(String.format(SMS_REPEAT_LIMIT_KEY,send.getKey(),key),send.getPhones(),60, TimeUnit.SECONDS);

    }

    /**
     * 同一个手机号 限制每天发送上限
     * @param send
     */
    private void phoneMaxNumLimit (SmsSend send){

        //批量短信不限制
        if(send.getPhones().length() > 15){
            return;
        }
        String date = DateUtil.formatDate(new Date());
        Object object = redisTemplate.opsForValue().get(String.format(SMS_PHONE_MAX_LIMIT_KEY,date,send.getPhones()));
        int total = object==null?0:(int)object;

        if(total  > Integer.MAX_VALUE){
            throw new ServiceException("手机号超出每日发送限制");
        }

        redisTemplate.opsForValue().set(String.format(SMS_PHONE_MAX_LIMIT_KEY,date,send.getPhones()),total+1,1,TimeUnit.DAYS);

    }

}
