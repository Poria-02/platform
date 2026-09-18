package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.enums.DictionaryType;
import cn.poria.upms.api.vo.DictQueryModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Optional;

public interface SysTreeDictService extends IService<SysTreeDict> {
   Optional<SysTreeDict> findById(String id);

   Optional<SysTreeDict> findUserDictById(String id);

   Optional<SysTreeDict> findByCode(String code);

   IPage<SysTreeDict> page(DictQueryModel queryModel);

   boolean removeDictById(String id);

   List<DictionaryType> getAllType();

   SysTreeDictItem saveItem(SysTreeDictItem item);

   List<SysTreeDictItem> findItemsByPid(String dictId, String pid);

   SysTreeDictItem updateItem(SysTreeDictItem item);

   Optional<SysTreeDictItem> findItem(String id);

   List<SysTreeDictItem> findItems(String dictId, String name);
}
