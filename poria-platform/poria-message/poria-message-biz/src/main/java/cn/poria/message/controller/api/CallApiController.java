package cn.poria.message.controller.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.CallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/call")
public class CallApiController {


    @Autowired
    private CallService callService;

    @Value("${follow.url}")
    private String url;

    @Inner(value = false)
    @PostMapping("/result")
    public JSONObject callResult(@RequestBody JSONObject request){
        log.info("外呼回推结果:{}",request.toString());
        callService.callResult(request.getStr("data"));
        String[] split = url.split(",");
        for (String s : split) {
            HttpRequest data = HttpUtil.createPost(s).body(request.toString());
            log.info("外推地址:{}",s);
            HttpResponse execute = data.execute();
        }
        return JSONUtil.createObj().set("code",0).set("message","SUCCESS");
    }

}
