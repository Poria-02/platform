package cn.poria.base.controller.api;

import cn.poria.base.entity.BaseBannerItem;
import cn.poria.base.service.BaseBannerItemService;
import cn.poria.base.service.BaseBannerService;
import cn.poria.base.vo.request.banner.BannerItemModel;
import cn.poria.base.vo.request.banner.BannerModel;
import cn.poria.base.vo.request.banner.BannerQueryModel;
import cn.poria.base.vo.response.banner.BannerVo;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import cn.poria.common.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * banner主表(BaseBanner)表控制层
 *
 * @author makejava
 * @since 2020-09-02 21:21:52
 */
@RestController
@Tag(name="banner管理(pc端)")
@RequestMapping("/api/banner")
public class BannerApiController {

    @Resource
    private BaseBannerService baseBannerService;

    @Resource
    private BaseBannerItemService baseBannerItemService;


    @Operation(summary = "分页查询", description = "分页查询(base_banner_page)")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('base_banner_page')")
    public R<IPage<BannerVo>> bannerPage(BannerQueryModel query) {

        return R.ok(baseBannerService.bannerPage(query));
    }


    @Operation(summary = "通过id查询", description = "通过id查询(base_banner_get)")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('base_banner_get')")
    public R selectBannerById(@PathVariable String id) {
        return R.ok(baseBannerService.getById(id));
    }


    @PostMapping
    @SysLog("新增banner主表" )
    @Operation(summary = "新增banner主表", description = "新增banner主表 权限：base_banner_add")
    @PreAuthorize("@pms.hasPermission('base_banner_add')" )
    public R saveBanner(@RequestBody @Validated(Create.class) BannerModel model) {
        baseBannerService.saveBanner(model);
        return R.ok();
    }


    @PutMapping
    @SysLog("修改banner主表" )
    @Operation(summary = "修改banner主表", description = "修改banner主表 权限：base_banner_edit")
    @PreAuthorize("@pms.hasPermission('base_banner_edit')" )
    public R updateBanner(@RequestBody @Validated(Update.class) BannerModel model) {
        baseBannerService.updateBanner(model);
        return R.ok();
    }


    @DeleteMapping("{id}")
    @SysLog("通过id删除banner主表" )
    @Operation(summary = "通过ID删除banner主表", description = "通过ID删除banner主表 权限：base_banner_del")
    @PreAuthorize("@pms.hasPermission('base_banner_del')" )
    public R deleteBannerById(@PathVariable String id) {
        baseBannerService.removeBanner(id);
        return R.ok();
    }


    @Operation(summary = "banner项分页查询", description = "banner项分页查询(base_banner_itemPage)")
    @GetMapping("/item/page/{bannerId}")
    @PreAuthorize("@pms.hasPermission('base_banner_itemPage')")
    public R selectAll(Page page ,@PathVariable("bannerId") String bannerId) {
        return R.ok(baseBannerItemService.page(page,new LambdaQueryWrapper<BaseBannerItem>().eq(BaseBannerItem::getBannerId,bannerId)));
    }

    @PostMapping("/item")
    @SysLog("新增banner项" )
    @Operation(summary = "新增banner项", description = "新增banner项 权限：base_bannerItem_add")
    @PreAuthorize("@pms.hasPermission('base_bannerItem_add')" )
    public R insertBannerItem(@RequestBody @Validated(Create.class) BannerItemModel model) {
        baseBannerItemService.saveBannerItem(model);
        return R.ok();
    }


    @PutMapping("/item")
    @SysLog("修改banner项" )
    @Operation(summary = "修改banner项", description = "修改banner项 权限：base_bannerItem_edit")
    @PreAuthorize("@pms.hasPermission('base_bannerItem_edit')" )
    public R updateBannerItem(@RequestBody @Validated(Update.class) BannerItemModel model) {
        baseBannerItemService.updateBannerItem(model);
        return R.ok();
    }


    @DeleteMapping("/item/{id}")
    @SysLog("通过id删除banner项" )
    @Operation(summary = "通过ID删除banner项", description = "通过ID删除banner项 权限:base_bannerItem_del")
    @PreAuthorize("@pms.hasPermission('base_bannerItem_del')" )
    @CacheEvict(value = CacheConstants.BANNER_ITEM, allEntries = true)
    public R deleteBannerItemById(@PathVariable String id) {
        baseBannerItemService.removeById(id);
        return R.ok();
    }
}

