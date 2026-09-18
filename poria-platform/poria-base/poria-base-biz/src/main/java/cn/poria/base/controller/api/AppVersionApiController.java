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
@Tag(name="APP版本管理")
@RequestMapping("/api/version")
public class AppVersionApiController {

    @Resource
    private BaseAppVersionService baseAppVersionService;


    @Operation(summary = "分页查询", description = "分页查询(base_version_page)")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('base_version_page')" )
    public R<IPage<AppVersionVo>> selectAll(AppVersionQueryModel queryModel) {

        Page page = new Page(queryModel.getCurrent(),queryModel.getSize());
        page.addOrder(OrderItem.desc("create_time"));

        LambdaQueryWrapper<BaseAppVersion> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(StrUtil.isNotBlank(queryModel.getAppName()),BaseAppVersion::getAppName,queryModel.getAppName());
        lambdaQueryWrapper.eq(StrUtil.isNotBlank(queryModel.getAppOs()),BaseAppVersion::getAppOs,queryModel.getAppOs());
        return R.ok(baseAppVersionService.page(page, lambdaQueryWrapper).convert(version ->{
            AppVersionVo vo = new AppVersionVo();
            BeanUtils.copyProperties(version,vo);
            return vo;
        }));
    }


    @Operation(summary = "通过id查询", description = "通过id查询(base_version_get)")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('base_version_get')" )
    public R<AppVersionVo> selectOne(@PathVariable String id) {
        return R.ok(baseAppVersionService.selectVersionDetial(id));
    }


    @PostMapping
    @SysLog("新增APP版本管理" )
    @Operation(summary = "新增APP版本管理", description = "新增APP版本管理 权限 base_version_add ")
    @PreAuthorize("@pms.hasPermission('base_version_add')" )
    public R insert(@RequestBody @Validated(Create.class) BaseAppVersionModel appVersionModel) {
        baseAppVersionService.saveAppVersion(appVersionModel);
        return R.ok();
    }


    @PutMapping
    @SysLog("修改APP版本管理" )
    @Operation(summary = "修改APP版本管理", description = "修改APP版本管理 权限 base_version_edit")
    @PreAuthorize("@pms.hasPermission('base_version_edit')" )
    public R update(@RequestBody @Validated(Create.class) BaseAppVersionModel appVersionModel) {
        baseAppVersionService.updateAppVersion(appVersionModel);
        return R.ok();
    }


    @DeleteMapping("{id}")
    @SysLog("通过id删除APP版本管理" )
    @Operation(summary = "通过ID删除APP版本管理", description = "通过ID删除APP版本管理 权限 base_version_del")
    @CacheEvict(value = CacheConstants.APP_VERSION, allEntries = true)
    @PreAuthorize("@pms.hasPermission('base_version_del')" )
    public R delete(@PathVariable String id) {
        baseAppVersionService.removeById(id);
        return R.ok();
    }
}

