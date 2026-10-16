package cn.poria.message.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.util.TemplateParamUtil;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SmsServiceManager {

    @Resource
    private MsgTemplateService templateService;

    /**
     * 短信发送日志服务对象
     */
    @Resource
    private MsgSmsLogService smsLogService;

    private final List<SmsService> smsServiceList;

    @Autowired
    private RedisTemplate redisTemplate;

    static final String SMS_REPEAT_LIMIT_KEY = "SMS_REPEAT_LIMIT_KEY:%s:%s";

    static final String SMS_MAX_TOTAL_LIMIT_KEY = "SMS_MAX_TOTAL_LIMIT_KEY:%s";

    static final String SMS_PHONE_MAX_LIMIT_KEY = "SMS_PHONE_MAX_LIMIT_KEY:%s:%s";

    public SmsServiceManager(List<SmsService> smsServices) {
        this.smsServiceList = smsServices;
    }

    public void sendSms(SmsSend req) {

        TemplateVo template = templateService.selectByKey(req.getKey());
        Assert.notNull(template, "模版:{}编码不存在", req.getKey());
        Assert.isTrue(TemplateType.SMS.getCode().equals(template.getType()), "模板类型必须为短信");
        if (template.getStatus() == 2) {
            log.info("模版{}已停用", template.getTitle());
            return;
        }
        limit(req);

        //动态设置模板参数
        Map<String, String> param = TemplateParamUtil.settingTemplateParam(template, req.getParam());
        req.setParam(param);

        for (SmsService smsService : smsServiceList) {
            if (smsService.supports(template.getPlatform())) {
                sendAndSaveLogs(smsService, req, template);
                return;
            }
        }
        throw new ServiceException("未找到短信平台[" + template.getPlatform() + "]");
    }

    /**
     * 获取通道发送结果后，按手机号集中保存日志
     *
     * @param smsService 已匹配的短信通道
     * @param req 短信发送参数
     * @param template 通用短信模板
     */
    private void sendAndSaveLogs(SmsService smsService, SmsSend req, TemplateVo template) {
        String content = templateService.getContentByKey(req.getKey(), req.getParam());
        String params = JSONUtil.toJsonStr(req.getParam());
        List<MsgSmsLog> logs = new ArrayList<>();
        List<String> failedPhones = new ArrayList<>();

        if (smsService.supportsBatch()) {
            R<String> result = sendWithResult(smsService, req, content);
            for (String phone : req.getPhones().split(",")) {
                logs.add(createLog(phone, req, template, content, params, result));
                if (!result.isSuccess()) {
                    failedPhones.add(phone);
                }
            }
        } else {
            for (String phone : req.getPhones().split(",")) {
                SmsSend singleReq = new SmsSend();
                BeanUtils.copyProperties(req, singleReq);
                singleReq.setPhones(phone);
                R<String> result = sendWithResult(smsService, singleReq, content);
                logs.add(createLog(phone, req, template, content, params, result));
                if (!result.isSuccess()) {
                    failedPhones.add(phone);
                }
            }
        }

        smsLogService.saveLogs(logs);
        if (!failedPhones.isEmpty()) {
            throw new ServiceException("短信发送失败，手机号：" + String.join(",", failedPhones));
        }
    }

    /**
     * 将通道异常统一转换为失败结果，供日志及调用方使用
     *
     * @param smsService 短信通道
     * @param req 短信发送参数
     * @param content 已渲染的短信内容或平台模板编码
     * @return 标准发送结果
     */
    private R<String> sendWithResult(SmsService smsService, SmsSend req, String content) {
        try {
            R<String> result = smsService.sendSms(req, content);
            return result != null ? result : R.failed("短信通道未返回发送结果");
        } catch (Exception e) {
            log.error("短信通道发送异常", e);
            return R.failed(StrUtil.blankToDefault(e.getMessage(), "短信通道发送异常"));
        }
    }

    /**
     * 构造单个手机号的发送日志
     *
     * @param phone 接收手机号
     * @param req 短信发送参数
     * @param template 通用短信模板
     * @param content 已渲染的短信内容或平台模板编码
     * @param params 发送参数快照
     * @param result 通道发送结果
     * @return 短信发送日志
     */
    private MsgSmsLog createLog(String phone, SmsSend req, TemplateVo template, String content, String params, R<String> result) {
        MsgSmsLog smsLog = new MsgSmsLog();
        smsLog.setContent(content);
        smsLog.setPhone(phone);
        smsLog.setTemplateKey(req.getKey());
        smsLog.setPlatform(template.getPlatform());
        smsLog.setResult(result.getData() != null ? result.getData() : result.getMsg());
        smsLog.setParams(params);
        smsLog.setCreateTime(new Date());
        smsLog.setStatus(result.isSuccess() ? 1 : 2);
        return smsLog;
    }

    /**
     * 限流操作
     *
     * @param send
     */
    private void limit(SmsSend send) {

        //每日短信发送总数限制
//        maxTotalLimit();

        //同一手机号 重复短信限制
        repeatLimit(send);

        //同一手机号,当天短信数量限制
        phoneMaxNumLimit(send);


    }

    /**
     * 每日最大数量限制
     */
    private void maxTotalLimit() {

        String date = DateUtil.formatDate(new Date());
        Object object = redisTemplate.opsForValue().get(String.format(SMS_MAX_TOTAL_LIMIT_KEY, date));
        int total = object == null ? 0 : (int) object;

        if (total >= 10) {
            throw new ServiceException("超出当日最大发送限制");
        }
        redisTemplate.opsForValue().set(String.format(SMS_MAX_TOTAL_LIMIT_KEY, date), total + 1, 1, TimeUnit.DAYS);

    }

    /**
     * 相同手机号 相同模版 一分钟之内不允许重复发送短信
     *
     * @param send
     */
    private void repeatLimit(SmsSend send) {

        String key = send.getPhones().length() > 15 ? "" + send.getPhones().hashCode() : send.getPhones();
        Object obj = redisTemplate.opsForValue().get(String.format(SMS_REPEAT_LIMIT_KEY, send.getKey(), key));
        if (send.getPhones().equals(obj)) {
            throw new ServiceException("重复发送,请稍后再试");
        }

        redisTemplate.opsForValue().set(String.format(SMS_REPEAT_LIMIT_KEY, send.getKey(), key), send.getPhones(), 60, TimeUnit.SECONDS);

    }

    /**
     * 同一个手机号 限制每天发送上限
     *
     * @param send
     */
    private void phoneMaxNumLimit(SmsSend send) {

        //批量短信不限制
        if (send.getPhones().length() > 15) {
            return;
        }
        String date = DateUtil.formatDate(new Date());
        Object object = redisTemplate.opsForValue().get(String.format(SMS_PHONE_MAX_LIMIT_KEY, date, send.getPhones()));
        int total = object == null ? 0 : (int) object;

        if (total >= 10) {
            throw new ServiceException("手机号超出每日发送限制");
        }

        redisTemplate.opsForValue().set(String.format(SMS_PHONE_MAX_LIMIT_KEY, date, send.getPhones()), total + 1, 1, TimeUnit.DAYS);

    }

}
