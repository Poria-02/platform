package cn.poria.message.controller.inner;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import cn.poria.message.service.WxService;
import cn.poria.message.vo.request.WxMsgSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/inner/wx")
public class WxInnerController {

    @Autowired
    private WxService wxService;


    /**
     * 发送微信模版消息
     * @param msgSend
     */
    @Inner
    @PostMapping("/sendTemplateMsg")
    public R sendTemplateMsg(@RequestBody @Validated WxMsgSend msgSend){
        wxService.sendTemplateMsg(msgSend);
        return R.ok();
    }

}
