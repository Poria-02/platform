package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
   name = "remoteRoleService",
   url = "${PORIA_UPMS:http://poria-upms:4000}"
)
public interface RemoteRoleService {
   @GetMapping({"/role/code/{code}"})
   R<SysRole> byCode(@PathVariable("code") String code, @RequestHeader("from") String from);
}
