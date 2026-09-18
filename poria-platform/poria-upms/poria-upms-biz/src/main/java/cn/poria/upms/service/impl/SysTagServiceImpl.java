package cn.poria.upms.service.impl;

import cn.poria.common.core.util.Assert;
import cn.poria.upms.api.entity.SysTag;
import cn.poria.upms.mapper.SysTagMapper;
import cn.poria.upms.service.SysTagService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import org.springframework.stereotype.Service;

@Service
public class SysTagServiceImpl extends ServiceImpl<SysTagMapper, SysTag> implements SysTagService {
   public SysTag saveTag(SysTag sysTag) {
      Assert.isNull(this.selectByKey(sysTag.getTagKey()), "key:{}已存在", new Object[]{sysTag.getTagKey()});
      this.save(sysTag);
      return sysTag;
   }

   public SysTag selectByKey(String key) {
      return (SysTag)this.getOne((Wrapper)(new LambdaQueryWrapper<SysTag>()).eq(SysTag::getTagKey, key));
   }

}
