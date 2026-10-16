package cn.poria.message.service.impl.template;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.message.api.RemoteAliMaService;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.integration.visual.vo.request.alima.TemplateMsgModel;
import cn.poria.message.service.TemplateMessageSender;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 支付宝小程序模板消息渠道
 */
@Service
public class AliMaTemplateMessageSender implements TemplateMessageSender {

    /**
     * 支付宝小程序消息接口
     */
    @Resource
    private RemoteAliMaService remoteAliMaService;

    @Override
    public String getAccountType() {
        return "ALI_MA";
    }

    @Override
    public String getTemplateType() {
        return TemplateType.MA.getCode();
    }

    @Override
    public boolean supports(String source) {
        return "ali_ma".equals(source);
    }

    @Override
    public String getAppId(TemplateVo template) {
        Assert.isTrue(JSONUtil.isTypeJSONObject(template.getContent()), "支付宝模板内容须为AppId与模板ID对应的JSON对象");
        JSONObject config = JSONUtil.parseObj(template.getContent());
        return config.size() == 1 ? config.keySet().iterator().next() : null;
    }

    @Override
    public String resolveRecipient(String accountId, String appId) {
        // TODO 接入业务账号与支付宝小程序的绑定关系后，填充该AppId下的支付宝用户标识
        return null;
    }

    @Override
    public void prepare(TemplateVo template, TemplateMessageModel model) {
        String templateId = JSONUtil.parseObj(template.getContent()).getStr(model.getAppId());
        Assert.isTrue(StrUtil.isNotBlank(templateId), "当前小程序未配置支付宝消息模板");
        model.setTemplateId(templateId);
    }

    @Override
    public R<?> send(TemplateMessageModel model) {
        TemplateMsgModel request = TemplateMsgModel.builder()
                .appId(model.getAppId()).userId(model.getOpenId()).templateId(model.getTemplateId())
                .url(model.getUrl()).param(model.getParam()).build();
        return remoteAliMaService.sendTemplateMessage(request, SecurityConstants.FROM_IN);
    }
}
