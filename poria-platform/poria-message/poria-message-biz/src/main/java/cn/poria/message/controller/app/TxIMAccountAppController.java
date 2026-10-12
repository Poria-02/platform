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

@Slf4j
@RestController
@Tag(name = "【App】- 腾讯账号管理")
@RequestMapping("/app/im/account")
public class TxIMAccountAppController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    @Lazy
    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;

    @Operation(summary = "获取用户登录凭据sig",description = "获取用户登录凭据sig")
    @GetMapping("/sig")
    public R<String> getUserSig(){
        Assert.notBlank(SecurityUtils.getSId(),"未查询到当前账户信息");
        return R.ok(txIMMsgSendUtil.getSig(SecurityUtils.getSId()));
    }

    @Operation(summary = "注销账号",description = "注销账号")
    @DeleteMapping()
    public R deleteAccountIds(){
        imService.deleteAccount(Arrays.asList(SecurityUtils.getSId()));
        return R.ok();
    }

}
