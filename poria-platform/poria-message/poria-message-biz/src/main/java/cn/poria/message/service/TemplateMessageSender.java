package cn.poria.message.service;

import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.vo.response.template.TemplateVo;

import java.util.List;

/**
 * 模板消息渠道接口，新增渠道通过独立实现接入
 */
public interface TemplateMessageSender {

    /**
     * 获取第三方账号类型
     *
     * @return 与UPMS第三方账号一致的类型编码
     */
    String getAccountType();

    /**
     * 获取支持的模板类型
     *
     * @return 现有消息模板类型编码
     */
    String getTemplateType();

    /**
     * 根据来源判断是否支持发送
     *
     * @param source 消息来源
     * @return 是否由当前实现处理
     */
    boolean supports(String source);

    /**
     * 获取模板明确绑定的唯一AppId
     *
     * @param template 现有模板配置
     * @return 模板仅配置一个应用时返回AppId，否则返回null
     */
    String getAppId(TemplateVo template);

    /**
     * 查询接收账号在当前渠道应用下的用户标识
     *
     * @param accountId 业务接收账号
     * @param appId 已选定的发送应用AppId
     * @return 微信OpenId或对应平台的用户标识，尚未接入时返回null
     */
    String resolveRecipient(String accountId, String appId);

    /**
     * 查询需要向同一用户发送消息的应用列表
     *
     * @param accountId 业务接收账号
     * @return 当前渠道中该用户已绑定且允许推送的AppId列表
     */
    default List<String> resolveAppIds(String accountId) {
        throw new ServiceException("当前消息来源不支持多应用发送");
    }

    /**
     * 按渠道解析平台模板和字段映射
     *
     * @param template 现有模板配置
     * @param model 应用及业务参数，解析结果写入此对象
     */
    void prepare(TemplateVo template, TemplateMessageModel model);

    /**
     * 将已解析的模板消息提交给对应平台
     *
     * @param model 应用、接收人、平台模板及映射后的参数
     * @return 平台发送结果
     */
    R<?> send(TemplateMessageModel model);
}
