package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysTag;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysTagService extends IService<SysTag> {
   SysTag saveTag(SysTag sysTag);

   SysTag selectByKey(String key);
}
