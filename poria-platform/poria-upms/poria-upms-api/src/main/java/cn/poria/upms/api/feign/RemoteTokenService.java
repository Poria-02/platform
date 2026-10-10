package cn.poria.upms.api.feign;

import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "remoteTokenService", url = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteTokenService {
    @PostMapping({"/token/page"})
    R<Page> getTokenPage(@RequestBody Map<String, Object> params, @RequestHeader("from") String from);

    @DeleteMapping({"/token/{token}"})
    R<Boolean> removeTokenById(@PathVariable("token") String token, @RequestHeader("from") String from);
}
