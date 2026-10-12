package cn.poria.message.controller.inner;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Slf4j
@Hidden
@Tag(name = "消息模板管理")
@RestController
@RequestMapping("/inner/template")
public class TemplateInnerController {

    /**
     * 服务对象
     */
    @Resource
    private MsgTemplateService msgTemplateService;

    @Inner
    @GetMapping("/key/{key}")
    public R<TemplateVo> getTemplateByKey(@PathVariable("key")String key){
        return R.ok(msgTemplateService.selectByKey(key));
    }

}
