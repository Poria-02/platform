package cn.poria.upms.service.impl;

import cn.poria.common.core.constant.enums.DictTypeEnum;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysPublicParam;
import cn.poria.upms.mapper.SysPublicParamMapper;
import cn.poria.upms.service.SysPublicParamService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SysPublicParamServiceImpl extends ServiceImpl<SysPublicParamMapper, SysPublicParam> implements SysPublicParamService {
   @Cacheable(
      value = {"params_details"},
      key = "#publicKey",
      unless = "#result == null "
   )
   public String getSysPublicParamKeyToValue(String publicKey) {
      SysPublicParam sysPublicParam = (SysPublicParam)((SysPublicParamMapper)this.baseMapper).selectOne((Wrapper)Wrappers.<SysPublicParam>lambdaQuery().eq(SysPublicParam::getPublicKey, publicKey));
      return sysPublicParam != null ? sysPublicParam.getPublicValue() : null;
   }

   @CacheEvict(
      value = {"params_details"},
      key = "#sysPublicParam.publicKey"
   )
   public R updateParam(SysPublicParam sysPublicParam) {
      SysPublicParam param = (SysPublicParam)this.getById(sysPublicParam.getPublicId());
      if (DictTypeEnum.SYSTEM.getType().equals(param.getSystem())) {
         return R.failed("系统内置参数不能删除");
      } else {
         SysPublicParam one = (SysPublicParam)this.getOne((Wrapper)(new LambdaQueryWrapper<SysPublicParam>()).eq(SysPublicParam::getPublicKey, sysPublicParam.getPublicKey()));
         return one != null && !one.getPublicId().equals(param.getPublicId()) ? R.failed("键重复，请核对后添加") : R.ok(this.updateById(sysPublicParam));
      }
   }

   @CacheEvict(
      value = {"params_details"},
      allEntries = true
   )
   public R removeParam(Long publicId) {
      SysPublicParam param = (SysPublicParam)this.getById(publicId);
      return DictTypeEnum.SYSTEM.getType().equals(param.getSystem()) ? R.failed("系统内置参数不能删除") : R.ok(this.removeById(publicId));
   }

   public R saveParam(SysPublicParam sysPublicParam) {
      SysPublicParam one = (SysPublicParam)this.getOne((Wrapper)(new LambdaQueryWrapper<SysPublicParam>()).eq(SysPublicParam::getPublicKey, sysPublicParam.getPublicKey()));
      if (one != null) {
         throw new ServiceException("键重复，请核对后添加");
      } else {
         return R.ok(this.save(sysPublicParam));
      }
   }

}
