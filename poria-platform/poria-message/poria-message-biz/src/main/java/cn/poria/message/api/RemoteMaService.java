package cn.poria.message.api;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.integration.visual.vo.request.ma.MaAccountModel;
import cn.poria.message.integration.visual.vo.response.ma.MaAccountVo;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** 消息服务使用的远程接口契约，保留原有 HTTP 路径和响应格式。 */
@Hidden
@FeignClient(name = "remoteMaService", url = "${PORIA_WXMA:http://poria-wxma:8023}")
public interface RemoteMaService {
    @PostMapping("/inner/user/send/msg")
	R<String> sendMaSubScribeMsg(@RequestBody TemplateMessageModel templateMessageModel, @RequestHeader(SecurityConstants.FROM)String from);

    @PostMapping("/inner/account/info")
	R<MaAccountVo> findMaAccountInfo(@RequestBody MaAccountModel model, @RequestHeader(SecurityConstants.FROM)String from);
}
