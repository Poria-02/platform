package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysTagItem;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysTagItemService extends IService<SysTagItem> {
   SysTagItem saveTagItem(SysTagItem sysTagItem);

   void updateTagItem(SysTagItem sysTagItem);

   List<SysTagItem> selectByKeys(String tagKey);
}
