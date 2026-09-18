package cn.poria.upms.service;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysDictItemService extends IService<SysDictItem> {
   R removeDictItem(String id);

   R updateDictItem(SysDictItem item);

   SysDictItem findDictItem(String dictCode, String value);
}
