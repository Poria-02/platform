package cn.poria.message.api;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.integration.visual.vo.request.alima.TemplateMsgModel;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** 消息服务使用的远程接口契约，保留原有 HTTP 路径和响应格式。 */
@Hidden
@FeignClient(name = "remoteAliMaService", url = "${PORIA_ALIMA:http://poria-alima:8034}")
public interface RemoteAliMaService {
    @PostMapping("/inner/msg/template/send")
	R sendTemplateMessage(@RequestBody TemplateMsgModel model,
								 @RequestHeader(SecurityConstants.FROM) String from);
}
