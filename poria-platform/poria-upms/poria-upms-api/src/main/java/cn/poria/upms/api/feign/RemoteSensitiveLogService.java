package cn.poria.upms.api.feign;

import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.dto.SensitiveInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "remoteSensitiveLogService", url = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteSensitiveLogService {
    @PostMapping({"/sensitive/log"})
    R saveSensitiveLog(@RequestBody @Validated SensitiveInfo sensitiveInfo);
}
