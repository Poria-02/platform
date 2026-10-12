package cn.poria.message.api;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** 消息服务使用的远程接口契约，保留原有 HTTP 路径和响应格式。 */
@Hidden
@FeignClient(name = "remoteStaffService", url = "${PORIA_STAFF:http://poria-staff:8005}")
public interface RemoteStaffService {
    @PostMapping("/inner/staffInfo/realNameAuth/result")
    R updateRealNameAuthResult(@RequestBody JSONObject jsonObject, @RequestHeader(SecurityConstants.FROM) String from);
}
