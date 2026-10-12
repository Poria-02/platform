package cn.poria.message.controller.app;


import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * author qiaodi
 * date 2025/11/27 17:47
 * version 6.8.0
 * description
 */
@Slf4j
@RestController
@Tag(name = "【App】- 阿里语音识别")
@RequestMapping("/app/asr")
public class AliAsrAppController {

    @Value("${ali.asr-apikey}")
    private String apikey;

    @GetMapping("/apikey")
    @Operation(summary = "获取api key", description = "获取api key")
    public R getAsr(){

        HttpResponse response = HttpUtil.createPost("https://dashscope.aliyuncs.com/api/v1/tokens")
                .header("Authorization", "Bearer " + apikey)
                .form("expire_in_seconds", 1800)
                .execute();

        log.info("阿里语音识别返回：{}", response.body());
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        if (jsonObject.get("token") != null){
            return R.ok(jsonObject);
        }
        return R.failed(jsonObject);
    }
}
