package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.video.VideoService;
import cn.poria.message.vo.request.im.ImCreateCloudRecordModel;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@Slf4j
@Hidden
@RestController
@RequestMapping("/inner/im/video")
public class IMVideoInnerController {

    @Autowired
    @Qualifier("txVideoService")
    private VideoService videoService;

    @Inner
    @PostMapping("/createCloudRecord")
    public R createCloudRecording(@RequestBody ImCreateCloudRecordModel model){
        videoService.createCloudRecording(model);
        return R.ok();
    }

    @Inner
    @Operation(summary = "删除录屏文件",description = "删除录屏文件")
    @DeleteMapping("/file/{id}")
    public R delRoomRecFile(@PathVariable("id") String id){
        videoService.delVodFile(id);
        return R.ok();
    }

}
