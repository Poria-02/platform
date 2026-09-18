package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysOauthClientDetails;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "remoteClientDetailsService", url = "${PORIA_UPMS:http://poria-upms:4000}")
public interface RemoteClientDetailsService {
   @GetMapping(value = {"/client/getClientDetailsById/{clientId}"},headers = {"from=Y"})
   R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);

   @GetMapping(value = {"/client/list"},headers = {"from=Y"})
   R<List<SysOauthClientDetails>> listClientDetails();
}
