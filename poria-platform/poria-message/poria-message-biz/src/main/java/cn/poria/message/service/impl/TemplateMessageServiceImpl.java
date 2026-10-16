package cn.poria.message.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.MsgWxLogService;
import cn.poria.message.service.TemplateMessageSender;
import cn.poria.message.service.TemplateMessageService;
import cn.poria.message.util.TemplateParamUtil;
import cn.poria.message.vo.request.template.TemplateMessageSendModel;
import cn.poria.message.vo.response.template.TemplateVo;
import cn.poria.upms.api.feign.RemoteSocialDetailsService;
import cn.poria.upms.api.vo.request.social.SocialDetailsQueryModel;
import cn.poria.upms.api.vo.response.social.SocialDetailsVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 按来源匹配发送实现，统一处理模板、账号绑定、参数和发送日志
 */
@Slf4j
@Service
public class TemplateMessageServiceImpl implements TemplateMessageService {

    private static final Pattern URL_VARIABLE = Pattern.compile("\\$\\{(.+?)}");

    /**
     * 已接入的模板消息发送实现
     */
    private final List<TemplateMessageSender> senders;

    /**
     * 通用模板配置服务
     */
    @Resource
    private MsgTemplateService templateService;

    /**
     * 第三方账号查询接口
     */
    @Resource
    private RemoteSocialDetailsService remoteSocialDetailsService;

    /**
     * 发送日志服务，日志独立提交
     */
    @Resource
    private MsgWxLogService wxLogService;

    /**
     * 注册渠道实现，后续新增渠道无需修改分发逻辑
     *
     * @param channels 已接入的发送渠道
     */
    public TemplateMessageServiceImpl(List<TemplateMessageSender> channels) {
        this.senders = List.copyOf(channels);
    }

    @Override
    public String send(TemplateMessageSendModel model) {
        MsgWxLog sendLog = new MsgWxLog();
        sendLog.setTemplateKey(model.getKey());
        sendLog.setAccountId(model.getAccountId());
        Map<String, String> param = new LinkedHashMap<>();
        if (model.getParam() != null) {
            param.putAll(model.getParam());
        }
        TemplateMessageModel message = new TemplateMessageModel();
        message.setParam(param);
        String channelType = null;
        R<?> result;
        try {
            TemplateMessageSender sender = findSender(model.getSource());
            channelType = sender.getAccountType();
            TemplateVo template = templateService.selectByKey(model.getKey());
            Assert.notNull(template, "模板不存在");
            sendLog.setContent(template.getContent());
            Assert.isTrue(sender.getTemplateType().equals(template.getType()), "模板类型与消息来源不匹配");
            Assert.isTrue(Integer.valueOf(1).equals(template.getStatus()), "模板已停用");

            SocialDetailsQueryModel query = new SocialDetailsQueryModel();
            query.setType(channelType);
            query.setAppId(StrUtil.isNotBlank(model.getAppId()) ? model.getAppId() : sender.getAppId(template));
            R<SocialDetailsVo> accountResult = remoteSocialDetailsService.account(query, SecurityConstants.FROM_IN);
            Assert.notNull(accountResult, "UPMS未返回第三方账号查询结果");
            Assert.isTrue(accountResult.isSuccess(), "第三方账号查询失败：{}", StrUtil.blankToDefault(accountResult.getMsg(), "UPMS返回失败"));
            SocialDetailsVo account = Assert.notNull(accountResult.getData(), "UPMS未返回第三方账号信息");
            Assert.isTrue(channelType.equals(account.getType()) && StrUtil.isNotBlank(account.getAppId()), "第三方账号类型或AppId不匹配");
            Assert.isTrue(StrUtil.isBlank(query.getAppId()) || query.getAppId().equals(account.getAppId()), "第三方账号AppId与模板绑定不匹配");
            message.setAppId(account.getAppId());
            param.put("appId", account.getAppId());
            if (StrUtil.isNotBlank(template.getParamMapping())) {
                TemplateParamUtil.settingTemplateParam(template, param);
                param.put("appId", account.getAppId());
            }
            message.setUrl(StrUtil.isBlank(template.getUrl()) ? null : URL_VARIABLE.matcher(template.getUrl())
                    .replaceAll(match -> Matcher.quoteReplacement(StrUtil.nullToDefault(param.get(match.group(1)), ""))));
            sender.prepare(template, message);
            message.setOpenId(sender.resolveRecipient(model.getAccountId(), account.getAppId()));
            Assert.isTrue(StrUtil.isNotBlank(message.getOpenId()), "接收账号与发送应用的绑定查询尚未接入");
            result = sender.send(message);
            Assert.notNull(result, "模板消息渠道未返回发送结果");
        } catch (Exception e) {
            log.error("模板消息发送失败，模板Key：{}，接收账号：{}", model.getKey(), model.getAccountId(), e);
            result = R.failed(StrUtil.blankToDefault(e.getMessage(), "模板消息发送失败"));
        }

        String resultMessage = result.isSuccess() ? result.getMsg() : StrUtil.blankToDefault(result.getMsg(), "模板消息发送失败");
        sendLog.setStatus(result.isSuccess() ? 1 : 2);
        sendLog.setResult(resultMessage);
        sendLog.setParams(JSONUtil.createObj().set("source", model.getSource()).set("channel", channelType).set("appId", message.getAppId())
                .set("templateId", message.getTemplateId()).set("param", message.getParam()).toString());
        if (result.isSuccess() && result.getData() != null) {
            sendLog.setMsgId(String.valueOf(result.getData()));
        }
        wxLogService.saveLogs(sendLog);
        if (!result.isSuccess()) {
            throw new ServiceException(resultMessage);
        }
        return sendLog.getMsgId();
    }

    @Override
    public Map<String, R<String>> sendAll(TemplateMessageSendModel model) {
        List<String> appIds = findSender(model.getSource()).resolveAppIds(model.getAccountId());
        Assert.notNull(appIds, "消息渠道未返回用户绑定的应用列表");
        Map<String, R<String>> results = new LinkedHashMap<>();
        for (String appId : appIds.stream().filter(StrUtil::isNotBlank).distinct().toList()) {
            TemplateMessageSendModel request = new TemplateMessageSendModel();
            BeanUtils.copyProperties(model, request);
            request.setAppId(appId);
            try {
                results.put(appId, R.ok(this.send(request)));
            } catch (Exception e) {
                log.error("多应用模板消息发送失败，模板Key：{}，AppId：{}", model.getKey(), appId, e);
                results.put(appId, R.failed(StrUtil.blankToDefault(e.getMessage(), "模板消息发送失败")));
            }
        }
        return results;
    }

    /**
     * 按消息来源查找唯一的发送实现
     *
     * @param source 消息来源
     * @return 匹配的渠道实现
     */
    private TemplateMessageSender findSender(String source) {
        List<TemplateMessageSender> matched = senders.stream().filter(sender -> sender.supports(source)).toList();
        Assert.isTrue(!matched.isEmpty(), "未找到模板消息来源[{}]", source);
        Assert.isTrue(matched.size() == 1, "模板消息来源[{}]匹配到多个发送实现", source);
        return matched.getFirst();
    }

}
