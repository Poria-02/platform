package cn.poria.message.service.impl.template;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.message.api.RemoteMaService;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.service.TemplateMessageSender;
import cn.poria.message.util.MaTemplateUtil;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 微信小程序订阅消息渠道
 */
@Service
public class MiniTemplateMessageSender implements TemplateMessageSender {

    /**
     * 微信小程序消息接口
     */
    @Resource
    private RemoteMaService remoteMaService;

    @Override
    public String getAccountType() {
        return "MINI";
    }

    @Override
    public String getTemplateType() {
        return TemplateType.MA.getCode();
    }

    @Override
    public boolean supports(String source) {
        return "weixin_ma".equals(source);
    }

    @Override
    public String getAppId(TemplateVo template) {
        Assert.isTrue(JSONUtil.isTypeJSONArray(template.getContent()), "小程序模板内容须为JSON数组");
        JSONArray configs = JSONUtil.parseArray(template.getContent());
        if (configs.size() == 1 && configs.getFirst() instanceof JSONObject config) {
            return config.getStr("appId");
        }
        return null;
    }

    @Override
    public String resolveRecipient(String accountId, String appId) {
        // TODO 接入业务账号与微信小程序的绑定关系后，填充该AppId下的OpenId
        return null;
    }

    @Override
    public void prepare(TemplateVo template, TemplateMessageModel model) {
        JSONObject config = MaTemplateUtil.getConfig(template.getContent(), model.getAppId());
        Assert.notNull(config, "当前小程序未配置订阅消息模板");
        String templateId = config.getStr("wxTemplateId");
        Assert.isTrue(StrUtil.isNotBlank(templateId), "小程序订阅消息模板ID未配置");
        model.setTemplateId(templateId);
        model.setParam(MaTemplateUtil.matchParam(config.getJSONArray("config"), model.getParam()));
    }

    @Override
    public R<?> send(TemplateMessageModel model) {
        return remoteMaService.sendMaSubScribeMsg(model, SecurityConstants.FROM_IN);
    }
}
