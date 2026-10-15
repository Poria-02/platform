package cn.poria.message.controller.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.message.entity.MsgImTemplate;
import cn.poria.message.service.MsgImTemplateService;
import cn.poria.message.vo.request.im.IMTemplateModel;
import cn.poria.message.vo.request.im.IMTemplateQueryModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * App端IM消息模板控制层
 *
 * @author wuning
 * @since 2025-05-07 10:47:07
 */
@Slf4j
@RestController
@Tag(name = "IM消息模板管理-APP")
@RequestMapping("/msg/imTemplate")
public class MsgImTemplateAppController {

    /**
     * IM消息模板服务对象
     */
    @Resource
    private MsgImTemplateService msgImTemplateService;

    /**
     * 分页查询IM消息模板
     *
     * @param queryModel 分页及筛选条件
     * @return IM消息模板分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询IM消息模板", description = "分页查询IM消息模板")
    public R selectAll(@ParameterObject IMTemplateQueryModel queryModel) {
        log.info("分页查询IM消息模板:{}", JSONUtil.toJsonStr(queryModel));
        return R.ok(msgImTemplateService.queryPage(queryModel));
    }

    /**
     * 新增IM消息模板
     *
     * @param model IM消息模板内容
     * @return IM消息模板ID
     */
    @SysLog("新增IM消息模板")
    @PostMapping
    @Operation(summary = "新增IM消息模板", description = "新增IM消息模板")
    public R<String> insert(@RequestBody @Validated IMTemplateModel model) {
        log.info("新增IM消息模板:{}", JSONUtil.toJsonStr(model));
        MsgImTemplate msgTemplate = BeanUtil.copyProperties(model, MsgImTemplate.class);
        msgImTemplateService.save(msgTemplate);
        return R.ok(msgTemplate.getId());
    }

    /**
     * 通过ID删除IM消息模板
     *
     * @param id IM消息模板ID
     * @return 是否删除成功
     */
    @SysLog("通过id删除IM消息模板")
    @DeleteMapping("{id}")
    @Operation(summary = "通过ID删除IM消息模板", description = "通过ID删除IM消息模板")
    public R<Boolean> delete(@PathVariable String id) {
        log.info("通过id删除IM消息模板:{}", id);
        return R.ok(msgImTemplateService.removeById(id));
    }
}
