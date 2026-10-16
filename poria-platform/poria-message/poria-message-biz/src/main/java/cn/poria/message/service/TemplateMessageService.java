package cn.poria.message.service;

import cn.poria.common.core.util.R;
import cn.poria.message.vo.request.template.TemplateMessageSendModel;

import java.util.Map;

/**
 * 按来源分发的模板消息发送服务
 */
public interface TemplateMessageService {

    /**
     * 根据来源匹配实现，解析模板及参数后发送消息并记录日志
     *
     * @param model 来源、模板Key、接收账号及业务参数
     * @return 平台消息标识，平台未提供时返回null
     */
    String send(TemplateMessageSendModel model);

    /**
     * 从当前渠道绑定的多个应用分别向同一用户发送模板消息
     *
     * @param model 来源、模板Key、接收账号及业务参数
     * @return 各AppId对应的发送结果，单个应用失败不影响其他应用
     */
    Map<String, R<String>> sendAll(TemplateMessageSendModel model);
}
