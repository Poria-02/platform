package cn.poria.message.controller.app;

import cn.poria.common.core.util.R;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.vo.request.ma.MaTemplateQueryModel;
import cn.poria.message.vo.request.template.TemplateQueryModel;
import cn.poria.message.vo.response.template.TemplateVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * App端消息模板查询控制层
 */
@Slf4j
@Tag(name = "【App】- 消息模板")
@RestController
@RequestMapping("/app/template")
public class TemplateAppController {

    @Resource
    private MsgTemplateService msgTemplateService;

    @PostMapping("/ma/list")
    @Operation(summary = "查询指定微信小程序已启用的订阅消息模板ID", description = "查询指定微信小程序已启用的订阅消息模板ID")
    public R<Map<String, String>> getMaTemplateIds(@RequestBody MaTemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.getMaTemplateIds(queryModel));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询消息模板", description = "分页查询消息模板")
    public R<IPage<TemplateVo>> selectAll(TemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.page(queryModel));
    }
}
