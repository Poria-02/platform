package cn.poria.message.controller.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.response.CallBackResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * im api
 * @author zhangchunlei
 * @date 2021年12月27日 11:28 上午
 */
@Hidden
@Slf4j
@RestController
@RequestMapping("/api/im")
public class IMApiController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 腾讯im回调
     */
    @Hidden
    @Inner(false)
    @PostMapping("/tx/callback")
    public CallBackResponse callBack(@RequestParam("SdkAppid") String appId,
                                     @RequestParam("CallbackCommand") String command,
                                     @RequestParam("contenttype") String contentType,
                                     @RequestParam("ClientIP") String clientIp,
                                     @RequestParam("OptPlatform") String optPlatForm,
                                     @RequestParam(value = "RequestTime", required = false) String requestTime,
                                     @RequestParam(value = "Sign", required = false) String sign,
                                     @RequestBody JSONObject  body){
      log.info("tx callBack msg : \n url param: {} \n body: {}", StrUtil.format("SdkAppid={}&CallbackCommand={}&contenttype={}&ClientIP={}&OptPlatform={}",appId,command,contentType,clientIp,optPlatForm),body);
      return imService.callBackMsgResolve(appId, command, requestTime, sign, body);
    }
}
