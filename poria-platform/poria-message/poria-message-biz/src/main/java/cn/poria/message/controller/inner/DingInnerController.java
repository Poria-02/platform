package cn.poria.message.controller.inner;

import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import cn.poria.message.service.DingService;
import cn.poria.message.vo.request.DingSend;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/inner/dd")
public class DingInnerController {

    @Autowired
    private DingService dingService;



    @Inner
    @PostMapping("/sendText")
    public R sendDingText(@RequestBody DingSend dingSend){
        dingService.sendDingText(dingSend);
        return R.ok();
    }

}
