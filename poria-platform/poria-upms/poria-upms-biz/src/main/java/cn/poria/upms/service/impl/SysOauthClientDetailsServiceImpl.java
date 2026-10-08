package cn.poria.upms.service.impl;

import cn.poria.upms.api.entity.SysOauthClientDetails;
import cn.poria.upms.mapper.SysOauthClientDetailsMapper;
import cn.poria.upms.service.SysOauthClientDetailsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class SysOauthClientDetailsServiceImpl extends ServiceImpl<SysOauthClientDetailsMapper, SysOauthClientDetails> implements SysOauthClientDetailsService {

    @CacheEvict(value = {"oauth:client:details"}, key = "#clientId")
    public Boolean removeByClientId(String clientId) {
        return this.remove(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId));
    }

    @CacheEvict(value = {"oauth:client:details"}, key = "#clientDetails.clientId")
    public Boolean updateClientById(SysOauthClientDetails clientDetails) {
        return this.updateById(clientDetails);
    }

    @CacheEvict(value = {"oauth:client:details"}, allEntries = true)
    public void clearClientCache() {
    }
}
