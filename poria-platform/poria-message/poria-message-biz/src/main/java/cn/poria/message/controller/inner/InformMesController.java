package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.config.MessageInformConfiguration;
import cn.poria.message.service.informMes.InformMessageHandler;
import cn.poria.message.vo.request.InformMesModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner/inform")
public class InformMesController {

    @Inner
    @Operation(summary = "消息发送", description = "消息发送")
    @PostMapping
    public R informMessage(@RequestBody @Validated InformMesModel model) {
        InformMessageHandler informHandler = MessageInformConfiguration.get(model.getInterveneModus());
        if(informHandler != null){
            informHandler.handlerInformMes(model);
        }
        return R.ok();
    }

}
