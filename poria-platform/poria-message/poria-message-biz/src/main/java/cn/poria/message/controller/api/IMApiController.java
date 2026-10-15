package cn.poria.message.controller.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.response.CallBackResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * 腾讯云IM回调控制层
 *
 * @author zhangchunlei
 * @date 2021年12月27日 11:28 上午
 */
@Hidden
@Slf4j
@RestController
@RequestMapping("/api/im")
public class IMApiController {

    /**
     * IM服务对象
     */
    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 接收并处理腾讯云IM回调
     *
     * @param appId IM应用ID
     * @param command 回调命令
     * @param contentType 回调内容格式
     * @param clientIp 客户端IP
     * @param optPlatForm 操作来源平台
     * @param requestTime 回调请求时间戳，单位为秒
     * @param sign 回调签名
     * @param body 回调消息内容
     * @return 腾讯云IM回调应答
     */
    @Hidden
    @Inner(false)
    @PostMapping("/tx/callback")
    @Operation(summary = "接收并处理腾讯云IM回调", description = "接收并处理腾讯云IM回调")
    public CallBackResponse callBack(@RequestParam("SdkAppid") String appId,
                                    @RequestParam("CallbackCommand") String command,
                                    @RequestParam("contenttype") String contentType,
                                    @RequestParam("ClientIP") String clientIp,
                                    @RequestParam("OptPlatform") String optPlatForm,
                                    @RequestParam(value = "RequestTime", required = false) String requestTime,
                                    @RequestParam(value = "Sign", required = false) String sign,
                                    @RequestBody JSONObject body) {
        log.info("tx callBack msg : \n url param: {} \n body: {}", StrUtil.format(
                "SdkAppid={}&CallbackCommand={}&contenttype={}&ClientIP={}&OptPlatform={}", appId, command, contentType, clientIp, optPlatForm), body);
        return imService.callBackMsgResolve(appId, command, requestTime, sign, body);
    }
}
