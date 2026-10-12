package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgMaParam;
import cn.poria.message.service.MsgMaParamService;
import cn.poria.message.service.MsgMaTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "【Api】- 小程序模版")
@RequestMapping("/api/ma/template")
public class MaTemplateController {

    @Autowired
    private MsgMaTemplateService maTemplateService;

    @Autowired
    private MsgMaParamService maParamService;

    @Operation(summary = "小程序模版列表",description = "小程序模版列表(message_matemplate_get)")
    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('message_matemplate_get')")
    public R list(){
        return R.ok(maTemplateService.list());
    }
    @Operation(summary = "小程序模版参数列表",description = "小程序模版参数列表(message_matemplate_param)")
    @GetMapping("/param/{key}")
    @PreAuthorize("@pms.hasPermission('message_matemplate_param')")
    public R params(@PathVariable("key")String key){
        return R.ok(maParamService.list(new LambdaQueryWrapper<MsgMaParam>().eq(MsgMaParam::getTemplateKey,key)));
    }

}
