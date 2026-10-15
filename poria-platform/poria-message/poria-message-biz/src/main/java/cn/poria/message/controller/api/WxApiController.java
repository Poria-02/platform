package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.message.service.WxService;
import cn.poria.message.vo.request.WxMsgSend;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理平台微信公众号消息发送控制层
 */
@Tag(name = "【Api】- 微信消息发送")
@RestController
@RequestMapping("/api/wx")
public class WxApiController {

    /**
     * 微信消息服务对象
     */
    @Autowired
    private WxService wxService;

    /**
     * 发送微信公众号模板消息
     *
     * @param msgSend 接收人、模板及消息参数
     * @return 发送结果
     */
    @PostMapping("/sendTemplateMsg")
    @PreAuthorize("@pms.hasPermission('wx_template_msg_send')")
    @Operation(summary = "发送微信公众号模板消息", description = "发送微信公众号模板消息 (权限：wx_template_msg_send)")
    public R sendTemplateMsg(@RequestBody @Validated WxMsgSend msgSend) {
        wxService.sendTemplateMsg(msgSend);
        return R.ok();
    }

    /**
     * 向指定用户关注的所有公众号发送模板消息
     *
     * @param msgSend 接收人、模板及消息参数
     * @return 发送结果
     */
    @PostMapping("/allWx/sendTemplateMsg")
    @PreAuthorize("@pms.hasPermission('wx_template_msg_send')")
    @Operation(summary = "向指定用户关注的所有公众号发送模板消息", description = "向指定用户关注的所有公众号发送模板消息 (权限：wx_template_msg_send)")
    public R sendAllWxTemplateMsg(@RequestBody @Validated WxMsgSend msgSend) {
        wxService.sendAllWxTemplateMsg(msgSend);
        return R.ok();
    }
}
