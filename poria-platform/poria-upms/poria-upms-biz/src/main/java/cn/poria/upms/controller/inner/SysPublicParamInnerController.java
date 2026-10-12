package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.service.SysPublicParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/param")
@Tag(name = "公共参数内部接口")
public class SysPublicParamInnerController {

    private final SysPublicParamService sysPublicParamService;

    @Inner(false)
    @Operation(summary = "查询公共参数值", description = "根据key查询公共参数值")
    @GetMapping({"/publicValue/{publicKey}"})
    public R publicKey(@PathVariable("publicKey") String publicKey) {
        return R.ok(this.sysPublicParamService.getSysPublicParamKeyToValue(publicKey));
    }
}
