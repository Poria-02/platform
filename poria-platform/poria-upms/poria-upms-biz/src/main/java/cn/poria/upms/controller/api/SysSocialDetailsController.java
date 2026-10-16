package cn.poria.upms.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import cn.poria.upms.api.vo.request.social.SocialDetailsModel;
import cn.poria.upms.api.vo.request.social.SocialDetailsQueryModel;
import cn.poria.upms.api.vo.response.social.SocialDetailsVo;
import cn.poria.upms.service.SysSocialDetailsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方账号配置管理接口
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "第三方账号信息")
@RequestMapping("/social/details")
public class SysSocialDetailsController {

    /**
     * 第三方账号配置服务
     */
    @Resource
    private final SysSocialDetailsService socialDetailsService;

    /**
     * 分页查询第三方账号
     *
     * @param model 分页及筛选条件
     * @return 不含密钥的账号分页数据
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('sys_social_details_page')")
    @Operation(summary = "分页查询第三方账号", description = "分页查询第三方账号 (权限：sys_social_details_page)")
    public R<IPage<SocialDetailsVo>> page(SocialDetailsQueryModel model) {
        return R.ok(this.socialDetailsService.pageAccounts(model));
    }

    /**
     * 新增第三方账号
     *
     * @param model 账号配置
     * @return 新增结果
     */
    @SysLog("新增第三方账号")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_social_details_add')")
    @Operation(summary = "新增第三方账号", description = "新增第三方账号 (权限：sys_social_details_add)")
    public R<Boolean> save(@Validated(Create.class) @RequestBody SocialDetailsModel model) {
        return R.ok(this.socialDetailsService.addAccount(model));
    }

    /**
     * 修改第三方账号，空密钥保留原值
     *
     * @param model 账号配置
     * @return 修改结果
     */
    @SysLog("修改第三方账号")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_social_details_edit')")
    @Operation(summary = "修改第三方账号", description = "修改第三方账号，空密钥保留原值 (权限：sys_social_details_edit)")
    public R<Boolean> update(@Validated(Update.class) @RequestBody SocialDetailsModel model) {
        return R.ok(this.socialDetailsService.updateAccount(model));
    }

    /**
     * 删除第三方账号
     *
     * @param id 账号ID
     * @return 删除结果
     */
    @SysLog("删除第三方账号")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_social_details_del')")
    @Operation(summary = "删除第三方账号", description = "删除第三方账号 (权限：sys_social_details_del)")
    public R<Boolean> remove(@PathVariable Integer id) {
        return R.ok(this.socialDetailsService.removeById(id));
    }
}
