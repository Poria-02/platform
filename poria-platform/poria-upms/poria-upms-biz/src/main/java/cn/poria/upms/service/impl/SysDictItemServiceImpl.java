package cn.poria.upms.service.impl;

import cn.poria.common.core.constant.enums.DictTypeEnum;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDict;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.mapper.SysDictItemMapper;
import cn.poria.upms.mapper.SysDictMapper;
import cn.poria.upms.service.SysDictItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    private final SysDictMapper sysDictMapper;

    @CacheEvict(value = {"dict_details"}, allEntries = true)
    public R removeDictItem(String id) {
        SysDictItem dictItem = this.getById(id);
        SysDict dict = this.sysDictMapper.selectById(dictItem.getDictId());
        return DictTypeEnum.SYSTEM.getType().equals(dict.getSystem()) ? R.failed("系统内置字典项目不能删除") : R.ok(this.removeById(id));
    }

    @CacheEvict(value = {"dict_details"}, allEntries = true)
    public R updateDictItem(SysDictItem item) {
        SysDict dict = this.sysDictMapper.selectById(item.getDictId());
        return DictTypeEnum.SYSTEM.getType().equals(dict.getSystem()) ? R.failed("系统内置字典项目不能删除") : R.ok(this.updateById(item));
    }

    public SysDictItem findDictItem(String dictCode, String value) {
        SysDict dict = this.sysDictMapper.selectOne(new LambdaQueryWrapper<SysDict>().eq(SysDict::getType, dictCode));
        return this.getOne(new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictId, dict.getId()).eq(SysDictItem::getValue, value));
    }
}
