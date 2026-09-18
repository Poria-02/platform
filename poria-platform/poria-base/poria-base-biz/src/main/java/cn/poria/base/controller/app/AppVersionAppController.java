package cn.poria.base.controller.app;


import cn.poria.base.service.BaseAppVersionService;
import cn.poria.base.vo.response.version.CheckVersionVo;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (BaseAppVersion)表控制层
 *
 * @author makejava
 * @since 2020-08-31 17:56:26
 */
@RestController
@Tag(name="APP版本管理(移动端)")
@RequestMapping("/app/version")
public class AppVersionAppController {
    
    @Resource
    private BaseAppVersionService baseAppVersionService;

    @Inner(value = false)
    @Operation(summary = "校验app版本",description = "校验APP版本")
    @GetMapping("/checkVersion/{appName}/{appOs}/{version}")
    public R<CheckVersionVo> checkVersion(@PathVariable("appName") String appName,
                                          @PathVariable("appOs") String appOs,
                                          @PathVariable("version") String version){
        return R.ok(baseAppVersionService.checkVersion(appName,appOs,version));
    }


}
