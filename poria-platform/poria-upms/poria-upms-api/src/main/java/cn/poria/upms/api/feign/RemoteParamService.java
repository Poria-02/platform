package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
   name = "remoteParamService",
   url = "${PORIA_UPMS:http://poria-upms:4000}"
)
public interface RemoteParamService {
   @GetMapping({"/param/publicValue/{publicKey}"})
   R<String> publicKey(@PathVariable("publicKey") String publicKey);
}
