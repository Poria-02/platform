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
 * App端通用消息模板查询控制层
 */
@Slf4j
@Tag(name = "【App】- 消息模板")
@RestController
@RequestMapping("/app/template")
public class TemplateAppController {

    /**
     * 通用消息模板服务对象
     */
    @Resource
    private MsgTemplateService msgTemplateService;

    /**
     * 查询指定小程序的服务通知模板ID
     *
     * @param queryModel 小程序AppID及模板Key列表
     * @return 模板Key与小程序模板ID的对应关系
     */
    @PostMapping("/ma/list")
    @Operation(summary = "查询指定小程序的服务通知模板ID", description = "查询指定小程序的服务通知模板ID")
    public R<Map<String, String>> getMaTemplateIds(@RequestBody MaTemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.getMaTemplateIds(queryModel));
    }

    /**
     * 分页查询通用消息模板
     *
     * @param queryModel 分页及筛选条件
     * @return 消息模板分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询通用消息模板", description = "分页查询通用消息模板")
    public R<IPage<TemplateVo>> selectAll(TemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.page(queryModel));
    }
}
