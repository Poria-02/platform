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
@Tag(name = "banner管理(pc端)")
@RequestMapping("/api/banner")
public class BannerApiController {

    @Resource
    private BaseBannerService baseBannerService;

    @Resource
    private BaseBannerItemService baseBannerItemService;

    /**
     * 分页查询轮播图
     *
     * @param query 分页及轮播图筛选条件
     * @return 轮播图分页数据
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('base_banner_page')")
    @Operation(summary = "分页查询", description = "分页查询 (权限：base_banner_page)")
    public R<IPage<BannerVo>> bannerPage(BannerQueryModel query) {

        return R.ok(baseBannerService.bannerPage(query));
    }

    /**
     * 查询轮播图详情
     *
     * @param id 轮播图ID
     * @return 轮播图详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('base_banner_get')")
    @Operation(summary = "通过id查询", description = "通过id查询 (权限：base_banner_get)")
    public R selectBannerById(@PathVariable String id) {
        return R.ok(baseBannerService.getById(id));
    }

    /**
     * 新增轮播图
     *
     * @param model 轮播图配置
     * @return 新增结果
     */
    @SysLog("新增banner主表")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('base_banner_add')")
    @Operation(summary = "新增banner主表", description = "新增banner主表 (权限：base_banner_add)")
    public R saveBanner(@RequestBody @Validated(Create.class) BannerModel model) {
        baseBannerService.saveBanner(model);
        return R.ok();
    }

    /**
     * 修改轮播图
     *
     * @param model 轮播图配置
     * @return 修改结果
     */
    @SysLog("修改banner主表")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('base_banner_edit')")
    @Operation(summary = "修改banner主表", description = "修改banner主表 (权限：base_banner_edit)")
    public R updateBanner(@RequestBody @Validated(Update.class) BannerModel model) {
        baseBannerService.updateBanner(model);
        return R.ok();
    }

    /**
     * 删除轮播图及其关联项
     *
     * @param id 轮播图ID
     * @return 删除结果
     */
    @SysLog("通过id删除banner主表")
    @DeleteMapping("{id}")
    @PreAuthorize("@pms.hasPermission('base_banner_del')")
    @Operation(summary = "通过ID删除banner主表", description = "通过ID删除banner主表 (权限：base_banner_del)")
    public R deleteBannerById(@PathVariable String id) {
        baseBannerService.removeBanner(id);
        return R.ok();
    }

    /**
     * 分页查询指定轮播图的展示项
     *
     * @param page 分页条件
     * @param bannerId 轮播图ID
     * @return 轮播图项分页数据
     */
    @GetMapping("/item/page/{bannerId}")
    @PreAuthorize("@pms.hasPermission('base_banner_itemPage')")
    @Operation(summary = "banner项分页查询", description = "banner项分页查询 (权限：base_banner_itemPage)")
    public R selectAll(Page page, @PathVariable("bannerId") String bannerId) {
        return R.ok(baseBannerItemService.page(page, new LambdaQueryWrapper<BaseBannerItem>().eq(BaseBannerItem::getBannerId, bannerId)));
    }

    /**
     * 新增轮播图展示项
     *
     * @param model 轮播图项配置
     * @return 新增结果
     */
    @SysLog("新增banner项")
    @PostMapping("/item")
    @PreAuthorize("@pms.hasPermission('base_bannerItem_add')")
    @Operation(summary = "新增banner项", description = "新增banner项 (权限：base_bannerItem_add)")
    public R insertBannerItem(@RequestBody @Validated(Create.class) BannerItemModel model) {
        baseBannerItemService.saveBannerItem(model);
        return R.ok();
    }

    /**
     * 修改轮播图展示项
     *
     * @param model 轮播图项配置
     * @return 修改结果
     */
    @SysLog("修改banner项")
    @PutMapping("/item")
    @PreAuthorize("@pms.hasPermission('base_bannerItem_edit')")
    @Operation(summary = "修改banner项", description = "修改banner项 (权限：base_bannerItem_edit)")
    public R updateBannerItem(@RequestBody @Validated(Update.class) BannerItemModel model) {
        baseBannerItemService.updateBannerItem(model);
        return R.ok();
    }

    /**
     * 删除轮播图展示项
     *
     * @param id 轮播图项ID
     * @return 删除结果
     */
    @SysLog("通过id删除banner项")
    @CacheEvict(value = CacheConstants.BANNER_ITEM, allEntries = true)
    @DeleteMapping("/item/{id}")
    @PreAuthorize("@pms.hasPermission('base_bannerItem_del')")
    @Operation(summary = "通过ID删除banner项", description = "通过ID删除banner项 (权限：base_bannerItem_del)")
    public R deleteBannerItemById(@PathVariable String id) {
        baseBannerItemService.removeById(id);
        return R.ok();
    }
}
