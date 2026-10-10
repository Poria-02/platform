package cn.poria.upms.api.feign;

import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysOauthClientDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "remoteClientDetailsService", url = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteClientDetailsService {
    @GetMapping(value = {"/client/getClientDetailsById/{clientId}"},headers = {"from=Y"})
    R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);

    @GetMapping(value = {"/client/list"},headers = {"from=Y"})
    R<List<SysOauthClientDetails>> listClientDetails();
}
