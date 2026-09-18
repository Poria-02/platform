package cn.poria.base.controller.app;


import cn.poria.base.entity.BaseActivity;
import cn.poria.base.service.BaseActivityService;
import cn.poria.base.vo.response.banner.BaseActivityVo;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="活动页管理 - app")
@RequestMapping("/app/baseActivity")
public class BaseActivityAppController {

    @Autowired
    private BaseActivityService baseActivityService;

    //根据id查询详情
    @Operation(summary = "根据id查看详情", description = "根据id查看详情")
    @GetMapping("/findById/{id}")
    public R<BaseActivityVo> findById(@PathVariable String id){
        BaseActivity baseActivity = baseActivityService.getById(id);
        BaseActivityVo baseActivityVo = new BaseActivityVo();
        BeanUtils.copyProperties(baseActivity, baseActivityVo);
        return R.ok(baseActivityVo);
    }
}

