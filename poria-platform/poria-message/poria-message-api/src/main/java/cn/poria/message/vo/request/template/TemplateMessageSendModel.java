package cn.poria.message.vo.request.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 按来源发送模板消息的公共参数
 */
@Data
public class TemplateMessageSendModel {

    @NotBlank(message = "key不能为空")
    @Schema(description = "需要发送的模板Key")
    private String key;

    @Schema(description = "业务参数列表")
    private Map<String, String> param;

    @NotBlank(message = "accountId不能为空")
    @Schema(description = "业务接收账号，用于查询其与发送应用的绑定关系")
    private String accountId;

    @Schema(description = "发送应用AppId，未指定时优先读取模板的唯一绑定，否则要求UPMS仅配置一个同类型账号")
    private String appId;

    @Schema(description = "消息来源：weixin_mp 公众号、weixin_ma 微信小程序、ali_ma 支付宝小程序；后续来源由对应实现扩展")
    private String source;
}
