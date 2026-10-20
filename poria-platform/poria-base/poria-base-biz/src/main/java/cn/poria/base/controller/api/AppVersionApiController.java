package cn.poria.base.controller.api;

import cn.hutool.core.util.StrUtil;
import cn.poria.base.entity.BaseAppVersion;
import cn.poria.base.service.BaseAppVersionService;
import cn.poria.base.vo.request.AppVersionQueryModel;
import cn.poria.base.vo.request.BaseAppVersionModel;
import cn.poria.base.vo.response.version.AppVersionVo;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import cn.poria.common.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * (BaseAppVersion)表控制层
 *
 * @author makejava
 * @since 2020-08-31 17:56:26
 */
@RestController
@Tag(name = "APP版本管理")
@RequestMapping("/api/version")
public class AppVersionApiController {

    @Resource
    private BaseAppVersionService baseAppVersionService;

    /**
     * 分页查询APP版本
     *
     * @param queryModel 分页及APP版本筛选条件
     * @return APP版本分页数据
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('base_version_page')")
    @Operation(summary = "分页查询", description = "分页查询 (权限：base_version_page)")
    public R<IPage<AppVersionVo>> selectAll(AppVersionQueryModel queryModel) {

        Page page = new Page(queryModel.getCurrent(), queryModel.getSize());
        page.addOrder(OrderItem.desc("create_time"));

        LambdaQueryWrapper<BaseAppVersion> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(StrUtil.isNotBlank(queryModel.getAppName()), BaseAppVersion::getAppName, queryModel.getAppName());
        lambdaQueryWrapper.eq(StrUtil.isNotBlank(queryModel.getAppOs()), BaseAppVersion::getAppOs, queryModel.getAppOs());
        return R.ok(baseAppVersionService.page(page, lambdaQueryWrapper).convert(version -> {
            AppVersionVo vo = new AppVersionVo();
            BeanUtils.copyProperties(version, vo);
            return vo;
        }));
    }

    /**
     * 查询APP版本详情
     *
     * @param id APP版本ID
     * @return APP版本详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('base_version_get')")
    @Operation(summary = "通过id查询", description = "通过id查询 (权限：base_version_get)")
    public R<AppVersionVo> selectOne(@PathVariable String id) {
        return R.ok(baseAppVersionService.selectVersionDetial(id));
    }

    /**
     * 新增APP版本
     *
     * @param appVersionModel APP版本配置
     * @return 新增结果
     */
    @SysLog("新增APP版本管理")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('base_version_add')")
    @Operation(summary = "新增APP版本管理", description = "新增APP版本管理 (权限：base_version_add)")
    public R insert(@RequestBody @Validated(Create.class) BaseAppVersionModel appVersionModel) {
        baseAppVersionService.saveAppVersion(appVersionModel);
        return R.ok();
    }

    /**
     * 修改APP版本
     *
     * @param appVersionModel APP版本配置
     * @return 修改结果
     */
    @SysLog("修改APP版本管理")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('base_version_edit')")
    @Operation(summary = "修改APP版本管理", description = "修改APP版本管理 (权限：base_version_edit)")
    public R update(@RequestBody @Validated(Update.class) BaseAppVersionModel appVersionModel) {
        baseAppVersionService.updateAppVersion(appVersionModel);
        return R.ok();
    }

    /**
     * 删除APP版本
     *
     * @param id APP版本ID
     * @return 删除结果
     */
    @SysLog("通过id删除APP版本管理")
    @CacheEvict(value = CacheConstants.APP_VERSION, allEntries = true)
    @DeleteMapping("{id}")
    @PreAuthorize("@pms.hasPermission('base_version_del')")
    @Operation(summary = "通过ID删除APP版本管理", description = "通过ID删除APP版本管理 (权限：base_version_del)")
    public R delete(@PathVariable String id) {
        baseAppVersionService.removeById(id);
        return R.ok();
    }
}
