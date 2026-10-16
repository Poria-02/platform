package cn.poria.message.service.impl.template;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.message.api.RemoteCustomerApi;
import cn.poria.message.api.RemoteMpService;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.integration.customer.vo.UserAccountBindVo;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.service.TemplateMessageSender;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 微信公众号模板消息渠道
 */
@Service
public class WxTemplateMessageSender implements TemplateMessageSender {

    /**
     * 公众号消息接口
     */
    @Resource
    private RemoteMpService remoteMpService;

    /**
     * 已有用户公众号绑定关系查询接口，绑定查询由当前渠道实现维护
     */
    @Resource
    private RemoteCustomerApi remoteCustomerApi;

    @Override
    public String getAccountType() {
        return "WX";
    }

    @Override
    public String getTemplateType() {
        return TemplateType.WX.getCode();
    }

    @Override
    public boolean supports(String source) {
        return "weixin_mp".equals(source);
    }

    @Override
    public String getAppId(TemplateVo template) {
        Assert.isTrue(JSONUtil.isTypeJSONObject(template.getContent()), "公众号模板内容须为AppId与模板ID对应的JSON对象");
        JSONObject config = JSONUtil.parseObj(template.getContent());
        return config.size() == 1 ? config.keySet().iterator().next() : null;
    }

    @Override
    public String resolveRecipient(String accountId, String appId) {
        // TODO 接入业务账号与公众号的绑定关系后，填充该AppId下的OpenId
        return null;
    }

    @Override
    public List<String> resolveAppIds(String accountId) {
        R<List<UserAccountBindVo>> result = remoteCustomerApi.findUserBinds(accountId, SecurityConstants.FROM_IN);
        Assert.notNull(result, "公众号绑定查询未返回结果");
        Assert.isTrue(result.isSuccess(), "公众号绑定查询失败：{}", StrUtil.blankToDefault(result.getMsg(), "绑定接口返回失败"));
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream().filter(bind -> bind != null && "1".equals(bind.getSubscribeStatus()))
                .map(UserAccountBindVo::getAppId).filter(StrUtil::isNotBlank).distinct().toList();
    }

    @Override
    public void prepare(TemplateVo template, TemplateMessageModel model) {
        String templateId = JSONUtil.parseObj(template.getContent()).getStr(model.getAppId());
        Assert.isTrue(StrUtil.isNotBlank(templateId), "当前公众号未配置消息模板");
        model.setTemplateId(templateId);
    }

    @Override
    public R<?> send(TemplateMessageModel model) {
        return remoteMpService.sendTemplateMessage(model, SecurityConstants.FROM_IN);
    }
}
