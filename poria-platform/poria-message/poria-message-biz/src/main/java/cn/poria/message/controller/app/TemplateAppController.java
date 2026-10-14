package cn.poria.message.controller.app;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.entity.MsgTemplate;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.vo.request.ma.MaTemplateQueryModel;
import cn.poria.message.vo.request.template.TemplateModel;
import cn.poria.message.vo.request.template.TemplateQueryModel;
import cn.poria.message.vo.response.template.TemplateVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "【App】- 消息模板")
@RestController
@RequestMapping("/app/template")
public class TemplateAppController {

    /**
     * 服务对象
     */
    @Resource
    private MsgTemplateService msgTemplateService;


    @Operation(summary = "查询服务通知模版id",description = "查询服务通知模版id")
    @PostMapping("/ma/list")
    public R getMaTemplateIds(@RequestBody MaTemplateQueryModel queryModel) {

        List<MsgTemplate> list = msgTemplateService.list(new LambdaQueryWrapper<MsgTemplate>().in(MsgTemplate::getTemplateKey,queryModel.getTemplateKey()).eq(MsgTemplate::getType,TemplateType.MA.getCode()));

        return R.ok(list.stream().filter(msgTemplate ->
                    StrUtil.contains(msgTemplate.getContent(),queryModel.getAppid())
                )
                .collect(Collectors.toMap(msgTemplate -> msgTemplate.getTemplateKey(),msgTemplate -> {
                    String templateId = null;
                    JSONArray json = JSONUtil.parseArray(msgTemplate.getContent());
                    for (int i = 0; i < json.size(); i++) {
                        JSONObject temp = json.getJSONObject(i);
                        if(StrUtil.equals(temp.getStr("appId"),queryModel.getAppid())){
                            templateId =  temp.getStr("wxTemplateId");
                            break;
                        }
                    }
                    return templateId;
                })));
    }

    @Operation(summary = "分页查询", description = "分页查询")
    @GetMapping("/page")
    public R<IPage<TemplateVo>> selectAll(TemplateQueryModel queryModel) {
        return R.ok(msgTemplateService.page(queryModel));
    }


}
