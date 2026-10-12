package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.service.SysDictItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dict")
@Tag(name = "字典内部接口")
public class SysDictInnerController {

    private final SysDictItemService sysDictItemService;

    @Inner(false)
    @GetMapping({"/type/{type}"})
    @Cacheable(value = {"dict_details"}, key = "#type", unless = "#result.data.isEmpty()")
    public R getDictByType(@PathVariable String type) {
        return R.ok(this.sysDictItemService.list(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getType, type)));
    }

    @Inner
    @Hidden
    @GetMapping({"/items/{type}"})
    public R<List<SysDictItem>> getSysDictItems(@PathVariable("type") String type) {
        return R.ok(this.sysDictItemService.list(new QueryWrapper<SysDictItem>().eq("type", type)));
    }

    @Inner
    @Hidden
    @GetMapping({"/item/{dictCode}/{value}"})
    public R<SysDictItem> findDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
        return R.ok(this.sysDictItemService.findDictItem(dictCode, value));
    }
}
