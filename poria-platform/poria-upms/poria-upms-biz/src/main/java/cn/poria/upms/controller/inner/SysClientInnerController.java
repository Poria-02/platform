package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysOauthClientDetails;
import cn.poria.upms.service.SysOauthClientDetailsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client")
@Tag(name = "终端内部接口")
public class SysClientInnerController {

    private final SysOauthClientDetailsService clientDetailsService;

    @Inner
    @GetMapping({"/getClientDetailsById/{clientId}"})
    public R getClientDetailsById(@PathVariable String clientId) {
        return R.ok(this.clientDetailsService.getOne(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false));
    }
}
