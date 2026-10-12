package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysLog;
import cn.poria.upms.service.SysLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/log")
@Tag(name = "日志内部接口")
public class SysLogInnerController {

    private final SysLogService sysLogService;

    @Inner
    @PostMapping({"/save"})
    public R save(@RequestBody @Valid SysLog sysLog) {
        return R.ok(this.sysLogService.save(sysLog));
    }
}
