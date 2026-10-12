package cn.poria.upms.controller.inner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.vo.response.treeDict.TreeDictItemVo;
import cn.poria.upms.service.SysTreeDictItemService;
import cn.poria.upms.service.SysTreeDictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/treedict/item")
@Tag(name = "分级字典项内部接口")
public class SysTreeDictItemInnerController {

    private final SysTreeDictService treeDictService;

    private final SysTreeDictItemService treeDictItemService;

    @Inner(false)
    @Operation(summary = "查询单个字典项", description = "查询单个字典项")
    @GetMapping({"/getItem/{dictId}/{value}"})
    public R<TreeDictItemVo> getDictItem(@PathVariable("dictId") String dictId, @PathVariable("value") String value) {
        SysTreeDictItem dictItem = this.treeDictItemService.getOne(new LambdaQueryWrapper<SysTreeDictItem>().eq(SysTreeDictItem::getDictId, dictId).eq(SysTreeDictItem::getValue, value));
        TreeDictItemVo treeDictItemVo = new TreeDictItemVo();
        BeanUtil.copyProperties(dictItem, treeDictItemVo);
        return R.ok(treeDictItemVo);
    }

    @Inner(false)
    @Operation(summary = "查询字典项(返回树)", description = "查询字典项(返回树)")
    @GetMapping({"/detail/{code}"})
    @Cacheable(value = {"tree_dict_details_tree"}, key = "#code", unless = "#result.data.isEmpty()")
    public R<List<TreeDictItemVo>> dictItemTrees(@PathVariable("code") String code) {
        Optional<SysTreeDict> dictOpt = this.treeDictService.findByCode(code);
        Assert.isTrue(dictOpt.isPresent(), "字典项不存在");
        List<SysTreeDictItem> list = this.treeDictService.findItems(dictOpt.get().getId(), null);
        List<TreeDictItemVo> beans = list.stream().map(this::toDictItemBean).collect(Collectors.toList());
        if (dictOpt.get().getIsTree() == 1) {
            beans = this.toTree(beans);
        }

        return R.ok(beans);
    }

    @Inner(false)
    @Operation(summary = "查询字典项(返回列表)", description = "查询字典项(返回列表)")
    @GetMapping({"/list/{code}"})
    @Cacheable(value = {"tree_dict_details_list"}, key = "#code", unless = "#result.data.isEmpty()")
    public R<List<TreeDictItemVo>> dictItems(@PathVariable("code") String code) {
        Optional<SysTreeDict> dictOpt = this.treeDictService.findByCode(code);
        Assert.isTrue(dictOpt.isPresent(), "字典项不存在");
        List<SysTreeDictItem> list = this.treeDictService.findItems(dictOpt.get().getId(), null);
        List<TreeDictItemVo> beans = list.stream().map(this::toDictItemBean).collect(Collectors.toList());
        return R.ok(beans);
    }

    @Schema(description = "查询上级字典项")
    @Inner
    @GetMapping({"/parent/{dictCode}/{value}"})
    public R<TreeDictItemVo> getParentDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
        SysTreeDictItem sysTreeDictItem = this.treeDictItemService.getParentDictItem(dictCode, value);
        TreeDictItemVo vo = new TreeDictItemVo();
        BeanUtils.copyProperties(sysTreeDictItem, vo);
        return R.ok(vo);
    }

    @Schema(description = "查询同级字典项")
    @Inner
    @GetMapping({"/peer/{dictCode}/{value}"})
    public R<TreeDictItemVo> getPeerDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
        SysTreeDictItem sysTreeDictItem = this.treeDictItemService.getPeerDictItem(dictCode, value);
        TreeDictItemVo vo = new TreeDictItemVo();
        BeanUtils.copyProperties(sysTreeDictItem, vo);
        return R.ok(vo);
    }

    @Inner
    @GetMapping({"/findByValue/{value}"})
    @Operation(summary = "查询字典职称", description = "查询字典职称")
    public R<TreeDictItemVo> findAuthenticate(@PathVariable("value") String value) {
        SysTreeDictItem treeDictItem = this.treeDictItemService.getOne(new LambdaQueryWrapper<SysTreeDictItem>().eq(SysTreeDictItem::getValue, value));
        if (treeDictItem != null) {
            TreeDictItemVo vo = new TreeDictItemVo();
            BeanUtils.copyProperties(treeDictItem, vo);
            return R.ok(vo);
        } else {
            return R.ok();
        }
    }

    private List<TreeDictItemVo> toTree(List<TreeDictItemVo> beans) {
        Map<String, TreeDictItemVo> map = beans.stream().collect(Collectors.toMap(TreeDictItemVo::getId, item -> item));

        for (TreeDictItemVo b : beans) {
            if (StrUtil.isNotBlank(b.getPid())) {
                TreeDictItemVo parent = map.get(b.getPid());
                if (parent != null) {
                    if (parent.getChilds() == null) {
                        parent.setChilds(new ArrayList<>());
                    }

                    parent.getChilds().add(b);
                }
            }
        }

        List<TreeDictItemVo> tree = new ArrayList<>();

        for (TreeDictItemVo b : beans) {
            if ("0".equals(b.getPid())) {
                tree.add(b);
            }
        }

        return tree;
    }

    private TreeDictItemVo toDictItemBean(SysTreeDictItem item) {
        TreeDictItemVo bean = new TreeDictItemVo();
        BeanUtils.copyProperties(item, bean);
        return bean;
    }
}
