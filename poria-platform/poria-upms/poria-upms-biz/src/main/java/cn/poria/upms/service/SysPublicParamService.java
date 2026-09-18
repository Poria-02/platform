package cn.poria.upms.service;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysPublicParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysPublicParamService extends IService<SysPublicParam> {
   String getSysPublicParamKeyToValue(String publicKey);

   R updateParam(SysPublicParam sysPublicParam);

   R removeParam(Long publicId);

   R saveParam(SysPublicParam sysPublicParam);
}
