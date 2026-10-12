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

@Slf4j
@Tag(name = "【Api】- 消息模板管理")
@RestController
@RequestMapping("/api/template")
public class TemplateApiController {

    /**
     * 服务对象
     */
    @Resource
    private MsgTemplateService msgTemplateService;

    /**
     * 分页查询所有数据
     *
     * @param queryModel 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询", description = "分页查询(message_template_page)")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('message_template_page')")
    public R<IPage<TemplateVo>> selectAll(TemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.page(queryModel));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过id查询", description = "通过id查询(message_template_get)")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('message_template_get')")
    public R<TemplateVo>selectOne(@PathVariable Serializable id) {
        MsgTemplate template = msgTemplateService.getById(id);
        Assert.notNull(template,"模版不存在");

        TemplateVo templateVo = new TemplateVo();
        BeanUtils.copyProperties(template,templateVo);
        return R.ok(templateVo);
    }

    /**
     * 新增消息模板
     * @param model 消息模板
     * @return R
     */
    @PostMapping
    @SysLog("新增消息模板" )
    @Operation(summary = "新增消息模板", description = "新增消息模板(权限：message_template_add)")
    @PreAuthorize("@pms.hasPermission('message_template_add')" )
    public R insert(@RequestBody @Validated TemplateModel model) {
        msgTemplateService.addTemplate(model);
        return R.ok();
    }

    /**
     * 修改数据
     *
     * @param model 实体对象
     * @return 修改结果
     */
    @PutMapping
    @SysLog("修改消息模板" )
    @Operation(summary = "修改消息模板", description = "修改消息模板 (权限：message_template_edit)")
    @PreAuthorize("@pms.hasPermission('message_template_edit')" )
    public R update(@RequestBody @Validated TemplateModel model) {
        msgTemplateService.updateTemplate(model);
        return R.ok();
    }


    @SysLog("启用停用" )
    @Operation(summary = "启用、停用", description = "启用、停用 (权限：message_template_status)")
    @PreAuthorize("@pms.hasPermission('message_template_status')" )
    @PostMapping("/changeStatus/{id}/{status}")
    public R changeStatus(@PathVariable("id")String id,@PathVariable("status")Integer status) {
        msgTemplateService.changeStatus(id,status);
        return R.ok();
    }

    /**
     * 删除数据
     *
     * @param id 主键结合
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @SysLog("通过id删除消息模板" )
    @Operation(summary = "通过ID删除消息模板", description = "通过ID删除消息模板 (权限：message_template_del)")
    @PreAuthorize("@pms.hasPermission('message_template_del')" )
    public R delete(@PathVariable String id) {
        msgTemplateService.deleteTemplate(id);
        return R.ok();
    }
}
