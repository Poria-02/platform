package cn.poria.upms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.upms.api.entity.SysTag;
import cn.poria.upms.api.entity.SysTagItem;
import cn.poria.upms.mapper.SysTagItemMapper;
import cn.poria.upms.service.SysTagItemService;
import cn.poria.upms.service.SysTagService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class SysTagItemServiceImpl extends ServiceImpl<SysTagItemMapper, SysTagItem> implements SysTagItemService {
   @Autowired
   private SysTagService tagService;

   public List<SysTagItem> selectByKeys(String tagKey) {
      SysTag tag = this.tagService.selectByKey(tagKey);
      Assert.notNull(tag, "标签{}分类不存在", new Object[]{tagKey});
      return this.list((Wrapper)(new LambdaQueryWrapper<SysTagItem>()).eq(SysTagItem::getTagId, tag.getId()));
   }

   @CacheEvict(
      value = {"tag_items"},
      allEntries = true
   )
   public SysTagItem saveTagItem(SysTagItem sysTagItem) {
      Assert.isNull(this.selectByTagIdAndValue(sysTagItem.getTagId(), sysTagItem.getValue()), "标签值:{}已存在", new Object[]{sysTagItem.getValue()});
      this.save(sysTagItem);
      return sysTagItem;
   }

   @CacheEvict(
      value = {"tag_items"},
      allEntries = true
   )
   public void updateTagItem(SysTagItem sysTagItem) {
      SysTagItem local = (SysTagItem)this.getById(sysTagItem.getId());
      Assert.notNull(local, "标签不存在", new Object[0]);
      if (!StrUtil.equals(sysTagItem.getValue(), local.getValue())) {
         Assert.isNull(this.selectByTagIdAndValue(sysTagItem.getTagId(), sysTagItem.getValue()), "标签值:{}已存在", new Object[]{sysTagItem.getValue()});
      }

      this.updateById(sysTagItem);
   }

   public SysTagItem selectByTagIdAndValue(String tagId, String value) {
      return (SysTagItem)this.getOne((Wrapper)((new LambdaQueryWrapper<SysTagItem>()).eq(SysTagItem::getTagId, tagId)).eq(SysTagItem::getValue, value));
   }

}
