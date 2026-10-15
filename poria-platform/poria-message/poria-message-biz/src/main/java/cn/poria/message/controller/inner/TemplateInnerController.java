package cn.poria.message.controller.inner;

import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 服务间通用消息模板查询控制层
 */
@Slf4j
@Hidden
@Tag(name = "消息模板管理")
@RestController
@RequestMapping("/inner/template")
public class TemplateInnerController {

    /**
     * 通用消息模板服务对象
     */
    @Resource
    private MsgTemplateService msgTemplateService;

    /**
     * 通过模板Key查询通用消息模板
     *
     * @param key 模板Key
     * @return 消息模板详情
     */
    @Inner
    @GetMapping("/key/{key}")
    @Operation(summary = "通过模板Key查询通用消息模板", description = "通过模板Key查询通用消息模板")
    public R<TemplateVo> getTemplateByKey(@PathVariable("key") String key) {
        return R.ok(msgTemplateService.selectByKey(key));
    }
}
