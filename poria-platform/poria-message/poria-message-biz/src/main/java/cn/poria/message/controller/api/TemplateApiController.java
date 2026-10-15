package cn.poria.message.controller.api;

import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.message.vo.request.template.TemplateModel;
import cn.poria.message.vo.request.template.TemplateQueryModel;
import cn.poria.message.vo.response.template.TemplateVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.poria.message.entity.MsgTemplate;
import cn.poria.message.service.MsgTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.io.Serializable;

/**
 * 管理平台通用消息模板控制层
 */
@Slf4j
@Tag(name = "【Api】- 消息模板管理")
@RestController
@RequestMapping("/api/template")
public class TemplateApiController {

    /**
     * 通用消息模板服务对象
     */
    @Resource
    private MsgTemplateService msgTemplateService;

    /**
     * 分页查询通用消息模板
     *
     * @param queryModel 分页及筛选条件
     * @return 消息模板分页数据
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('message_template_page')")
    @Operation(summary = "分页查询通用消息模板", description = "分页查询通用消息模板 (权限：message_template_page)")
    public R<IPage<TemplateVo>> selectAll(TemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.page(queryModel));
    }

    /**
     * 通过ID查询通用消息模板详情
     *
     * @param id 消息模板ID
     * @return 消息模板详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('message_template_get')")
    @Operation(summary = "通过ID查询通用消息模板详情", description = "通过ID查询通用消息模板详情 (权限：message_template_get)")
    public R<TemplateVo> selectOne(@PathVariable Serializable id) {
        MsgTemplate template = msgTemplateService.getById(id);
        Assert.notNull(template, "模版不存在");

        TemplateVo templateVo = new TemplateVo();
        BeanUtils.copyProperties(template, templateVo);
        return R.ok(templateVo);
    }

    /**
     * 新增通用消息模板
     *
     * @param model 消息模板内容
     * @return 新增结果
     */
    @SysLog("新增消息模板")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('message_template_add')")
    @Operation(summary = "新增通用消息模板", description = "新增通用消息模板 (权限：message_template_add)")
    public R insert(@RequestBody @Validated TemplateModel model) {
        msgTemplateService.addTemplate(model);
        return R.ok();
    }

    /**
     * 修改通用消息模板
     *
     * @param model 消息模板ID及模板内容
     * @return 修改结果
     */
    @SysLog("修改消息模板")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('message_template_edit')")
    @Operation(summary = "修改通用消息模板", description = "修改通用消息模板 (权限：message_template_edit)")
    public R update(@RequestBody @Validated TemplateModel model) {
        msgTemplateService.updateTemplate(model);
        return R.ok();
    }

    /**
     * 启用或停用通用消息模板
     *
     * @param id 消息模板ID
     * @param status 模板状态
     * @return 状态修改结果
     */
    @SysLog("启用停用")
    @PostMapping("/changeStatus/{id}/{status}")
    @PreAuthorize("@pms.hasPermission('message_template_status')")
    @Operation(summary = "启用或停用通用消息模板", description = "启用或停用通用消息模板 (权限：message_template_status)")
    public R changeStatus(@PathVariable("id") String id, @PathVariable("status") Integer status) {
        msgTemplateService.changeStatus(id, status);
        return R.ok();
    }

    /**
     * 通过ID删除通用消息模板
     *
     * @param id 消息模板ID
     * @return 删除结果
     */
    @SysLog("通过id删除消息模板")
    @DeleteMapping("{id}")
    @PreAuthorize("@pms.hasPermission('message_template_del')")
    @Operation(summary = "通过ID删除通用消息模板", description = "通过ID删除通用消息模板 (权限：message_template_del)")
    public R delete(@PathVariable String id) {
        msgTemplateService.deleteTemplate(id);
        return R.ok();
    }
}
