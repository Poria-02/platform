package cn.poria.message.controller.inner;

import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.TemplateMessageService;
import cn.poria.message.vo.request.template.TemplateMessageSendModel;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import java.util.Map;

/**
 * 服务间消息模板查询与发送控制层
 */
@Slf4j
@Hidden
@Tag(name = "消息模板管理")
@RestController
@RequestMapping("/inner/template")
public class TemplateInnerController {

    @Resource
    private MsgTemplateService msgTemplateService;

    @Resource
    private TemplateMessageService templateMessageService;

    @Inner
    @PostMapping("/send")
    @Operation(summary = "按来源发送模板消息", description = "匹配消息来源的实现，解析模板及参数并调用对应发送接口")
    public R<String> send(@RequestBody @Validated TemplateMessageSendModel model) {
        return R.ok(templateMessageService.send(model));
    }

    @Inner
    @PostMapping("/sendAll")
    @Operation(summary = "向同一用户发送多应用模板消息", description = "由渠道查询用户绑定的应用，逐个发送并返回各应用的结果")
    public R<Map<String, R<String>>> sendAll(@RequestBody @Validated TemplateMessageSendModel model) {
        return R.ok(templateMessageService.sendAll(model));
    }

    @Inner
    @GetMapping("/key/{key}")
    @Operation(summary = "通过模板Key查询消息模板", description = "通过模板Key查询消息模板")
    public R<TemplateVo> getTemplateByKey(@PathVariable("key") String key) {
        return R.ok(msgTemplateService.selectByKey(key));
    }
}
