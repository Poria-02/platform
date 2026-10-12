package cn.poria.upms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.enums.DictionaryType;
import cn.poria.upms.api.vo.ExportDictModel;
import cn.poria.upms.api.vo.SysTreeDictVO;
import cn.poria.upms.mapper.SysTreeDictItemMapper;
import cn.poria.upms.service.SysTreeDictItemService;
import cn.poria.upms.service.SysTreeDictService;
import cn.poria.upms.util.DictFileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysTreeDictItemServiceImpl extends ServiceImpl<SysTreeDictItemMapper, SysTreeDictItem> implements SysTreeDictItemService {

    @Resource
    private SysTreeDictService treeDictService;

    public SysTreeDictItem getParentDictItem(String dictCode, String value) {
        Optional<SysTreeDict> optional = this.treeDictService.findByCode(dictCode);
        Assert.isTrue(optional.isPresent(), "字典不存在{}", dictCode);
        String dictId = optional.get().getId();
        SysTreeDictItem sysTreeDictItem = this.getOne(new LambdaQueryWrapper<SysTreeDictItem>().eq(SysTreeDictItem::getDictId, dictId).eq(SysTreeDictItem::getValue, value));
        Assert.notNull(sysTreeDictItem, "字典项不存在");
        SysTreeDictItem parentItem = this.getOne(new LambdaQueryWrapper<SysTreeDictItem>().eq(SysTreeDictItem::getDictId, dictId).eq(SysTreeDictItem::getId, sysTreeDictItem.getPid()));
        Assert.notNull(parentItem, "上级字典科室不存在");
        return parentItem;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean removeTreeItemsById(String itemId) {
        SysTreeDictItem root = this.getById(itemId);
        if (root == null) {
            return false;
        }

        List<SysTreeDictItem> items = this.list(Wrappers.<SysTreeDictItem>lambdaQuery()
                .eq(SysTreeDictItem::getDictId, root.getDictId()));
        Map<String, List<String>> childrenByParentId = new HashMap<>();
        for (SysTreeDictItem item : items) {
            childrenByParentId.computeIfAbsent(item.getPid(), key -> new ArrayList<>()).add(item.getId());
        }

        Set<String> itemIds = new HashSet<>();
        ArrayDeque<String> pendingIds = new ArrayDeque<>();
        pendingIds.add(itemId);
        while (!pendingIds.isEmpty()) {
            String currentId = pendingIds.removeFirst();
            if (!itemIds.add(currentId)) {
                continue;
            }
            List<String> children = childrenByParentId.get(currentId);
            if (children != null) {
                pendingIds.addAll(children);
            }
        }

        return this.removeByIds(itemIds);
    }

    public SysTreeDictItem getPeerDictItem(String dictCode, String value) {
        Optional<SysTreeDict> optional = this.treeDictService.findByCode(dictCode);
        Assert.isTrue(optional.isPresent(), "字典不存在{}", dictCode);
        String dictId = optional.get().getId();
        SysTreeDictItem sysTreeDictItem = this.getOne(new LambdaQueryWrapper<SysTreeDictItem>().eq(SysTreeDictItem::getDictId, dictId).eq(SysTreeDictItem::getValue, value));
        Assert.notNull(sysTreeDictItem, "字典项不存在");
        return sysTreeDictItem;
    }

    public void exportDict(ExportDictModel exportDictModel, HttpServletResponse response) throws IOException {
        List<String> codes = exportDictModel.getDictTreeCode();
        List<SysTreeDict> dict = this.treeDictService.list(new LambdaQueryWrapper<SysTreeDict>()
                .in(codes != null && !codes.isEmpty(), SysTreeDict::getCode, codes)
                .eq(SysTreeDict::getIsDelete, 0).orderByAsc(SysTreeDict::getCode));
        List<String> ids = dict.stream().map(SysTreeDict::getId).toList();
        List<SysTreeDictItem> dictItem = ids.isEmpty() ? List.of() : this.list(
                new LambdaQueryWrapper<SysTreeDictItem>().in(SysTreeDictItem::getDictId, ids)
                        .eq(SysTreeDictItem::getIsDelete, 0).orderByAsc(SysTreeDictItem::getSort, SysTreeDictItem::getId));
        this.getFile(dict, dictItem, response);
    }

    private void getFile(List<SysTreeDict> dict, List<SysTreeDictItem> dictItem, HttpServletResponse response) throws IOException {
        List<SysTreeDictVO> vos = dict.stream().map((info) -> {
            SysTreeDictVO sysTreeDictVO = new SysTreeDictVO();
            BeanUtil.copyProperties(info, sysTreeDictVO);
            ArrayList<SysTreeDictItem> list = new ArrayList<>();

            for (SysTreeDictItem item : dictItem) {
                if (Objects.equals(item.getDictId(), info.getId())) {
                    list.add(item);
                }
            }

            sysTreeDictVO.setTreeItems(list);
            return sysTreeDictVO;
        }).collect(Collectors.toList());
        DictFileUtil.write(vos, "treedict", response);
    }

    @Transactional(rollbackFor = {Exception.class})
    @CacheEvict(value = {CacheConstants.TREE_DICT_DETAILS_LIST, CacheConstants.TREE_DICT_DETAILS_TREE,
            CacheConstants.TREE_DICT_DETAILS_CITY}, allEntries = true)
    public void importDict(MultipartFile file) throws IOException {
        List<SysTreeDictVO> dicts = DictFileUtil.read(file, "treedict", SysTreeDictVO.class);
        Set<String> codes = new HashSet<>();
        for (SysTreeDictVO info : dicts) {
            if (StrUtil.isBlank(info.getCode()) || StrUtil.isBlank(info.getName()) || !codes.add(info.getCode())) {
                throw new ServiceException("字典编码、名称不能为空，编码不能重复");
            }
            if (!Objects.equals(info.getType(), DictionaryType.SYSTEM.getId())
                    && !Objects.equals(info.getType(), DictionaryType.CUSTOMER.getId())) {
                throw new ServiceException("字典 " + info.getCode() + " 的字典类型无效");
            }
            if (!Objects.equals(info.getIsTree(), 0) && !Objects.equals(info.getIsTree(), 1)) {
                throw new ServiceException("字典 " + info.getCode() + " 的结构类型必须是 0（列表）或 1（树）");
            }
            info.setTreeItems(this.orderImportItems(info));
        }

        for (SysTreeDictVO info : dicts) {
            SysTreeDict existing = this.treeDictService.getOne(Wrappers.<SysTreeDict>lambdaQuery()
                    .eq(SysTreeDict::getCode, info.getCode()));
            SysTreeDict dict = new SysTreeDict();
            BeanUtil.copyProperties(info, dict, "id", "isDelete");
            dict.setId(existing == null ? null : existing.getId());
            dict.setIsDelete(0);
            if (!this.treeDictService.saveOrUpdate(dict)) {
                throw new ServiceException("保存字典失败：" + info.getCode());
            }
            this.remove(Wrappers.<SysTreeDictItem>lambdaQuery().eq(SysTreeDictItem::getDictId, dict.getId()));
            Map<String, String> importedIds = new HashMap<>();
            for (SysTreeDictItem source : info.getTreeItems()) {
                SysTreeDictItem item = new SysTreeDictItem();
                BeanUtil.copyProperties(source, item, "id", "dictId", "pid", "isDelete");
                item.setDictId(dict.getId());
                String parentId = this.importParentId(source, info.getIsTree());
                item.setPid("0".equals(parentId) ? "0" : importedIds.get(parentId));
                item.setIsDelete(0);
                if (!this.save(item)) {
                    throw new ServiceException("保存字典项失败：" + source.getName());
                }
                importedIds.put(source.getId(), item.getId());
            }
        }
        log.info("分级字典导入成功，数量：{}", dicts.size());
    }

    /** 先验证完整结构，再按父节点优先顺序导入，避免修改旧 ID 后丢失下级。 */
    private List<SysTreeDictItem> orderImportItems(SysTreeDictVO info) {
        List<SysTreeDictItem> items = info.getTreeItems() == null ? List.of() : info.getTreeItems();
        Map<String, SysTreeDictItem> byId = new HashMap<>();
        Map<String, List<SysTreeDictItem>> byParent = new HashMap<>();
        for (SysTreeDictItem item : items) {
            if (item == null || StrUtil.isBlank(item.getId()) || "0".equals(item.getId())
                    || StrUtil.isBlank(item.getName()) || byId.putIfAbsent(item.getId(), item) != null) {
                throw new ServiceException("字典 " + info.getCode() + " 的节点 ID、名称不能为空，ID 不能重复或为 0");
            }
            byParent.computeIfAbsent(this.importParentId(item, info.getIsTree()), key -> new ArrayList<>()).add(item);
        }
        for (String parentId : byParent.keySet()) {
            if (!"0".equals(parentId) && !byId.containsKey(parentId)) {
                throw new ServiceException("字典 " + info.getCode() + " 的父节点不存在：" + parentId);
            }
        }
        List<SysTreeDictItem> ordered = new ArrayList<>();
        ArrayDeque<SysTreeDictItem> pending = new ArrayDeque<>(byParent.getOrDefault("0", List.of()));
        while (!pending.isEmpty()) {
            SysTreeDictItem item = pending.removeFirst();
            ordered.add(item);
            pending.addAll(byParent.getOrDefault(item.getId(), List.of()));
        }
        if (ordered.size() != items.size()) {
            throw new ServiceException("字典 " + info.getCode() + " 的节点存在循环引用");
        }
        return ordered;
    }

    private String importParentId(SysTreeDictItem item, Integer isTree) {
        return Objects.equals(isTree, 0) || StrUtil.isBlank(item.getPid()) ? "0" : item.getPid();
    }
}
