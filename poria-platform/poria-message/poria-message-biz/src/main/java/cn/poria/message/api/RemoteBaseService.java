package cn.poria.message.api;

import cn.poria.base.feign.BaseFeignClient;
import cn.poria.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "remoteBaseService",url = ServiceNameConstants.BASE_SERVICE)
public interface RemoteBaseService extends BaseFeignClient {
}
