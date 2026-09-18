package cn.poria.base.controller.api;




import cn.poria.base.service.BaseActivityService;
import cn.poria.base.vo.request.banner.BannerQueryModel;
import cn.poria.base.vo.request.banner.BaseActivityModel;
import cn.poria.base.vo.response.banner.BannerVo;
import cn.poria.base.vo.response.banner.BaseActivityVo;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * (BaseActivity)表控制层
 *
 * @author makejava
 * @since 2025-11-27 15:33:02
 */
@RestController
@Tag(name="活动页管理 - pc端")
@RequestMapping("/api/baseActivity")
public class BaseActivityApiController {


    @Autowired
    private BaseActivityService baseActivityService;

    @Operation(summary = "分页查询", description = "分页查询(base_activity_page)")
    @PostMapping("/page")
    @PreAuthorize("@pms.hasPermission('base_activity_page')")
    public R<IPage<BaseActivityVo>> bannerPage(@RequestBody BaseActivityModel baseActivityModel) {
        return R.ok(baseActivityService.listPage(baseActivityModel));
    }

    @Operation(summary = "新增", description = "新增(base_activity_add)")
    @PostMapping("/add")
    @PreAuthorize("@pms.hasPermission('base_activity_add')")
    public R insert(@RequestBody BaseActivityModel baseActivityModel) {
        baseActivityService.saveActivity(baseActivityModel);
        return R.ok();
    }

    @Operation(summary = "删除", description = "删除(base_activity_delete)")
    @DeleteMapping("/delete")
    @PreAuthorize("@pms.hasPermission('base_activity_delete')")
    public R delete(@RequestBody BaseActivityModel baseActivityModel) {
        baseActivityService.removeById(baseActivityModel.getId());
        return R.ok();
    }

    @Operation(summary = "修改", description = "修改(base_activity_update)")
    @PutMapping("/update")
    @PreAuthorize("@pms.hasPermission('base_activity_update')")
    public R update(@RequestBody BaseActivityModel baseActivityModel) {
        baseActivityService.updateByIdActivity(baseActivityModel);
        return R.ok();
    }

}


