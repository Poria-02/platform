package cn.poria.upms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.data.datascope.DataScope;
import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.enums.DictionaryType;
import cn.poria.upms.api.vo.DictQueryModel;
import cn.poria.upms.mapper.SysTreeDictItemMapper;
import cn.poria.upms.mapper.SysTreeDictMapper;
import cn.poria.upms.service.SysTreeDictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysTreeDictServiceImpl extends ServiceImpl<SysTreeDictMapper, SysTreeDict> implements SysTreeDictService {
   @Autowired
   private SysTreeDictMapper treeDictMapper;
   @Autowired
   private SysTreeDictItemMapper treeDictItemMapper;

   public Optional<SysTreeDict> findById(String id) {
      return Optional.ofNullable((SysTreeDict)this.treeDictMapper.selectById(id));
   }

   public Optional<SysTreeDict> findUserDictById(String id) {
      return Optional.ofNullable(this.treeDictMapper.findById(id, new DataScope()));
   }

   public Optional<SysTreeDict> findByCode(String code) {
      LambdaQueryWrapper<SysTreeDict> where = Wrappers.lambdaQuery();
      where.eq(SysTreeDict::getCode, code);
      where.last("limit 1");
      return Optional.ofNullable((SysTreeDict)this.treeDictMapper.selectOne(where));
   }

   public IPage<SysTreeDict> page(DictQueryModel queryModel) {
      LambdaQueryWrapper<SysTreeDict> where = Wrappers.lambdaQuery();
      where.like(StrUtil.isNotBlank(queryModel.getCode()), SysTreeDict::getCode, queryModel.getCode());
      where.like(StrUtil.isNotBlank(queryModel.getName()), SysTreeDict::getName, queryModel.getName());
      where.eq(StrUtil.isNotBlank(queryModel.getType()), SysTreeDict::getType, queryModel.getType());
      IPage<SysTreeDict> page = new Page((long)queryModel.getCurrent(), (long)queryModel.getSize());
      return this.page(page, where);
   }

   @Transactional(rollbackFor = {Exception.class})
   @CacheEvict(value = {"tree_dict_details_list", "tree_dict_details_tree", "tree_dict_details_city"}, allEntries = true)
   public boolean removeDictById(String id) {
      this.treeDictItemMapper.delete(Wrappers.<SysTreeDictItem>lambdaQuery().eq(SysTreeDictItem::getDictId, id));
      return this.removeById(id);
   }

   public List<DictionaryType> getAllType() {
      return DictionaryType.ALL;
   }

   public List<SysTreeDictItem> findItems(String dictId, String name) {
      LambdaQueryWrapper<SysTreeDictItem> where = Wrappers.lambdaQuery();
      (where.eq(SysTreeDictItem::getDictId, dictId)).orderByAsc(SysTreeDictItem::getSort);
      if (!StrUtil.isEmpty(name)) {
         where.likeRight(SysTreeDictItem::getName, name);
      }

      return this.treeDictItemMapper.selectList(where);
   }

   public List<SysTreeDictItem> findItemsByPid(String dictId, String pid) {
      LambdaQueryWrapper<SysTreeDictItem> where = Wrappers.lambdaQuery();
      where.eq(SysTreeDictItem::getDictId, dictId);
      where.eq(SysTreeDictItem::getPid, pid);
      where.orderByAsc(SysTreeDictItem::getSort);
      return this.treeDictItemMapper.selectList(where);
   }

   public Optional<SysTreeDictItem> findItem(String id) {
      return Optional.ofNullable((SysTreeDictItem)this.treeDictItemMapper.selectById(id));
   }

   public SysTreeDictItem saveItem(SysTreeDictItem item) {
      item.setIsDelete(0);
      item.setCreateTime(new Date());
      item.setUpdateTime(item.getCreateTime());
      this.treeDictItemMapper.insert(item);
      return item;
   }

   public SysTreeDictItem updateItem(SysTreeDictItem item) {
      item.setUpdateTime(new Date());
      this.treeDictItemMapper.updateById(item);
      return item;
   }

}
