package cn.poria.base.controller.app;

import cn.poria.base.service.BaseBannerItemService;
import cn.poria.base.vo.response.banner.AppBannerItemVo;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * banner主表(BaseBanner)表控制层
 *
 * @author makejava
 * @since 2020-09-02 21:21:52
 */
@RestController
@Tag(name="banner管理(移动端)")
@RequestMapping("/app/banner")
public class BannerAppController {

    @Resource
    private BaseBannerItemService baseBannerItemService;

    @Operation(summary = "查询")
    @GetMapping("/items/{code}")
    @Cacheable(value = CacheConstants.BANNER_ITEM,key = "#code",unless = "#result.data.isEmpty()")
    public R<List<AppBannerItemVo>> getBannerItem(@PathVariable("code")String code){
        return R.ok(baseBannerItemService.getBannerItem(code));
    }

}
