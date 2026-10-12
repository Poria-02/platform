package cn.poria.message.controller.app;

import cn.hutool.json.JSONObject;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.message.service.video.VideoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author qiaodi
 * date 2025/7/15 16:42
 * version 6.7.1
 * description
 */
@Slf4j
@RestController
@Tag(name = "【App】- im一对一通话")
@RequestMapping("/app/im/call")
public class IMCallAppController {

    @Autowired
    @Qualifier("txVideoService")
    private VideoService videoService;

    @SysLog("视频通话-音视频通话")
    @PostMapping("/video/result")
    public R videoResult(@RequestBody JSONObject body){
        videoService.videoResultResolve(body);
        return R.ok();
    }
}
