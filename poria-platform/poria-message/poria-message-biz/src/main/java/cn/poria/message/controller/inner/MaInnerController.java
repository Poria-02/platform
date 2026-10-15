package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.WxService;
import cn.poria.message.vo.request.WxMsgSend;
import cn.poria.message.vo.request.ma.MaAccountMessageModel;
import cn.poria.message.vo.response.ma.MaAccountMessageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务间微信小程序消息与账号控制层
 *
 * @author zhangchunlei
 * @date 2021年12月20日 3:08 下午
 */
@Hidden
@RestController
@RequestMapping("/inner/ma")
public class MaInnerController {

    /**
     * 微信消息服务对象
     */
    @Autowired
    private WxService wxService;

    /**
     * 发送微信小程序订阅消息
     *
     * @param msgSend 接收人、模板及消息参数
     * @return 发送结果
     */
    @Inner
    @PostMapping("/sendMaSubScribeMsg")
    @Operation(summary = "发送微信小程序订阅消息", description = "发送微信小程序订阅消息")
    public R sendMaSubScribeMsg(@RequestBody @Validated WxMsgSend msgSend) {
        wxService.sendMaSubScribeMsg(msgSend);
        return R.ok();
    }

    /**
     * 根据筛选条件查询小程序账号
     *
     * @param model 组织ID及小程序AppID等筛选条件
     * @return 小程序账号信息
     */
    @Inner
    @PostMapping("/account/info")
    @Operation(summary = "根据筛选条件查询小程序账号", description = "根据筛选条件查询小程序账号")
    public R<MaAccountMessageVo> findMaAccountInfo(@RequestBody MaAccountMessageModel model) {
        return R.ok(wxService.findMaAccountInfo(model));
    }
}
