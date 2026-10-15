package cn.poria.message.controller.app;

import cn.poria.common.core.util.Assert;
import cn.poria.common.security.annotation.Inner;
import cn.poria.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * App端腾讯云IM账号控制层
 */
@Slf4j
@RestController
@Tag(name = "【App】- 腾讯账号管理")
@RequestMapping("/app/im/account")
public class TxIMAccountAppController {

    /**
     * IM服务对象
     */
    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 腾讯云IM请求工具
     */
    @Lazy
    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;

    /**
     * 获取当前用户的IM登录凭据
     *
     * @return IM登录凭据UserSig
     */
    @GetMapping("/sig")
    @Operation(summary = "获取当前用户的IM登录凭据", description = "获取当前用户的IM登录凭据")
    public R<String> getUserSig() {
        Assert.notBlank(SecurityUtils.getSId(), "未查询到当前账户信息");
        return R.ok(txIMMsgSendUtil.getSig(SecurityUtils.getSId()));
    }

    /**
     * 注销当前用户的IM账号
     *
     * @return 注销结果
     */
    @DeleteMapping()
    @Operation(summary = "注销当前用户的IM账号", description = "注销当前用户的IM账号")
    public R deleteAccountIds() {
        imService.deleteAccount(Arrays.asList(SecurityUtils.getSId()));
        return R.ok();
    }
}
