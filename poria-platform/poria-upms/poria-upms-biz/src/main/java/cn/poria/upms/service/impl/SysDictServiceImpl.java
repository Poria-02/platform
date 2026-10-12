package cn.poria.upms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.constant.CommonConstants;
import cn.poria.common.core.constant.enums.DictTypeEnum;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDict;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.api.vo.request.dict.ExportDictModel;
import cn.poria.upms.api.vo.response.dict.SysDictVO;
import cn.poria.upms.mapper.SysDictMapper;
import cn.poria.upms.service.SysDictItemService;
import cn.poria.upms.service.SysDictService;
import cn.poria.upms.util.DictFileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    private final SysDictItemService sysDictItemService;

    @CacheEvict(value = {"dict_details"}, allEntries = true)
    @Transactional(rollbackFor = {Exception.class})
    public R removeDict(String id) {
        SysDict dict = this.getById(id);
        if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystem())) {
            return R.failed("系统内置字典不能删除");
        } else {
            this.baseMapper.deleteById(id);
            this.sysDictItemService.remove(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
            return R.ok();
        }
    }

    public R updateDict(SysDict dict) {
        SysDict sysDict = this.getById(dict.getId());
        return DictTypeEnum.SYSTEM.getType().equals(sysDict.getSystem()) ? R.failed("系统内置字典不能修改") : R.ok(this.updateById(dict));
    }

    public void exportDict(ExportDictModel exportDictModel, HttpServletResponse response) throws IOException {
        List<String> types = exportDictModel.getDictTypes();
        List<SysDict> dict = this.list(new LambdaQueryWrapper<SysDict>()
                .in(types != null && !types.isEmpty(), SysDict::getType, types)
                .eq(SysDict::getDelFlag, CommonConstants.STATUS_NORMAL).orderByAsc(SysDict::getType));
        List<String> ids = dict.stream().map(SysDict::getId).toList();
        List<SysDictItem> dictItem = ids.isEmpty() ? List.of() : this.sysDictItemService.list(
                new LambdaQueryWrapper<SysDictItem>().in(SysDictItem::getDictId, ids)
                        .eq(SysDictItem::getDelFlag, CommonConstants.STATUS_NORMAL)
                        .orderByAsc(SysDictItem::getSort, SysDictItem::getId));
        this.getFile(dict, dictItem, response);
    }

    private void getFile(List<SysDict> dict, List<SysDictItem> dictItem, HttpServletResponse response) throws IOException {
        List<SysDictVO> vos = dict.stream().map((info) -> {
            SysDictVO sysDictVO = new SysDictVO();
            BeanUtil.copyProperties(info, sysDictVO);
            ArrayList<SysDictItem> list = new ArrayList<>();

            for (SysDictItem item : dictItem) {
                if (Objects.equals(item.getDictId(), info.getId())) {
                    list.add(item);
                }
            }

            sysDictVO.setItems(list);
            return sysDictVO;
        }).collect(Collectors.toList());
        DictFileUtil.write(vos, "dict", response);
    }

    @Transactional(rollbackFor = {Exception.class})
    @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    public void importDict(MultipartFile file) throws IOException {
        List<SysDictVO> dicts = DictFileUtil.read(file, "dict", SysDictVO.class);
        Set<String> types = new HashSet<>();
        for (SysDictVO info : dicts) {
            if (StrUtil.isBlank(info.getType()) || !types.add(info.getType())) {
                throw new ServiceException("字典类型不能为空或重复");
            }
            if (StrUtil.isBlank(info.getSystem())) {
                info.setSystem(DictTypeEnum.BIZ.getType());
            }
            if (!DictTypeEnum.SYSTEM.getType().equals(info.getSystem())
                    && !DictTypeEnum.BIZ.getType().equals(info.getSystem())) {
                throw new ServiceException("字典 " + info.getType() + " 的系统标记无效");
            }
            Set<String> values = new HashSet<>();
            if (info.getItems() == null) {
                info.setItems(List.of());
            }
            for (SysDictItem item : info.getItems()) {
                if (item == null || StrUtil.isBlank(item.getValue()) || StrUtil.isBlank(item.getLabel())
                        || !values.add(item.getValue())) {
                    throw new ServiceException("字典 " + info.getType() + " 的字典项名称、值不能为空，值不能重复");
                }
            }
        }

        for (SysDictVO info : dicts) {
            SysDict existing = this.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getType, info.getType()));
            SysDict dict = new SysDict();
            BeanUtil.copyProperties(info, dict, "id", "delFlag");
            dict.setId(existing == null ? null : existing.getId());
            dict.setDelFlag(CommonConstants.STATUS_NORMAL);
            if (!this.saveOrUpdate(dict)) {
                throw new ServiceException("保存字典失败：" + info.getType());
            }
            this.sysDictItemService.remove(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, dict.getId()));
            List<SysDictItem> items = new ArrayList<>();
            for (SysDictItem source : info.getItems()) {
                SysDictItem item = new SysDictItem();
                BeanUtil.copyProperties(source, item, "id", "dictId", "type", "delFlag");
                item.setDictId(dict.getId());
                item.setType(dict.getType());
                item.setDelFlag(CommonConstants.STATUS_NORMAL);
                items.add(item);
            }
            if (!items.isEmpty() && !this.sysDictItemService.saveBatch(items)) {
                throw new ServiceException("保存字典项失败：" + info.getType());
            }
        }
        log.info("字典导入成功，数量：{}", dicts.size());
    }
}
