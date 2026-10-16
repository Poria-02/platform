package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.vo.request.social.SocialDetailsQueryModel;
import cn.poria.upms.api.vo.response.social.SocialDetailsVo;
import cn.poria.upms.service.SysSocialDetailsService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 第三方账号服务间查询接口
 */
@Hidden
@RestController
@RequestMapping("/inner/social/details")
public class SysSocialDetailsInnerController {

    @Resource
    private SysSocialDetailsService socialDetailsService;

    /**
     * 查询第三方账号列表
     *
     * @param model 账号筛选条件
     * @return 不含密钥的账号列表
     */
    @Inner
    @PostMapping("/list")
    @Operation(summary = "查询第三方账号列表", description = "按类型及AppId查询第三方账号列表")
    public R<List<SocialDetailsVo>> list(@RequestBody SocialDetailsQueryModel model) {
        return R.ok(socialDetailsService.listAccounts(model));
    }

    /**
     * 选择唯一匹配的第三方账号
     *
     * @param model 账号类型及可选AppId
     * @return 不含密钥的账号信息
     */
    @Inner
    @PostMapping("/account")
    @Operation(summary = "查询第三方账号", description = "指定AppId时精确查询，未指定时要求该类型只有一个账号")
    public R<SocialDetailsVo> account(@RequestBody SocialDetailsQueryModel model) {
        return R.ok(socialDetailsService.selectAccount(model));
    }
}
