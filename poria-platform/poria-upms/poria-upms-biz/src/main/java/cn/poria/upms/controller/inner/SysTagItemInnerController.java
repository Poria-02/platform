package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysTagItem;
import cn.poria.upms.service.SysTagItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tagItem")
@Tag(name = "标签项内部接口")
public class SysTagItemInnerController {

    private final SysTagItemService sysTagItemService;

    @Inner
    @GetMapping({"/items/{tagKey}"})
    @Cacheable(value = {"tag_items"}, key = "#tagKey", unless = "#result.data.isEmpty()")
    public R<List<SysTagItem>> selectItemsByKeys(@PathVariable("tagKey") String tagKey) {
        return R.ok(this.sysTagItemService.selectByKeys(tagKey));
    }
}
