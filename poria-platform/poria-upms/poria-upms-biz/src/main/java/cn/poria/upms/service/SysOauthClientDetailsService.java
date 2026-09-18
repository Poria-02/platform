package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysOauthClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysOauthClientDetailsService extends IService<SysOauthClientDetails> {
   Boolean removeByClientId(String clientId);

   Boolean updateClientById(SysOauthClientDetails clientDetails);

   void clearClientCache();
}
