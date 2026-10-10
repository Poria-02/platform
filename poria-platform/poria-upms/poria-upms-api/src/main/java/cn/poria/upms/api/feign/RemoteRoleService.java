package cn.poria.upms.api.feign;

import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "remoteRoleService", url = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteRoleService {
    @GetMapping({"/role/code/{code}"})
    R<SysRole> byCode(@PathVariable("code") String code, @RequestHeader("from") String from);
}
