package cn.poria.message.controller.app;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.security.annotation.Inner;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.vo.request.im.ImCreateCloudRecordModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import cn.poria.message.service.video.VideoService;
import cn.poria.message.service.video.tx.bean.Room;
import cn.poria.message.service.video.tx.config.TxTRTCProperties;
import cn.poria.message.service.video.tx.util.PrivateMapKeyUtil;
import cn.poria.message.vo.request.trtc.DelRoomMemberModel;
import cn.poria.message.vo.request.trtc.IMVideoMeetingModel;
import cn.poria.message.vo.request.trtc.PrivateMapKeyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "【App】- 实时音视频通话管理")
@RestController
@RequestMapping("/app/im/video")
public class IMVideoAppController {

    @Autowired
    private TxTRTCProperties txTRTCProperties;

    @Autowired
    @Qualifier("txVideoService")
    private VideoService videoService;

    @Operation(summary = "获取用户权限Key", description = "获取用户权限Key")
    @PostMapping("/Key")
    public R getPrivateMapKey(PrivateMapKeyModel model){
        if(StrUtil.isBlank(model.getAccountId())){
            model.setAccountId(SecurityUtils.getSId());
        }
        Room room = videoService.getRoom(String.valueOf(model.getRoomId()));
        if(room == null){
            return R.failed("房间不存在");
        }
        PrivateMapKeyUtil privateMapKeyUtil = new PrivateMapKeyUtil(txTRTCProperties.getSdkAppId(),txTRTCProperties.getSecretKey());
        return R.ok(privateMapKeyUtil.genPrivateMapKey(model.getAccountId(), txTRTCProperties.getSingExpireTime(),model.getRoomId(),room.isMember(model.getAccountId()) ? 0 : 255 ));
    }

    @Operation(summary = "创建房间",description = "创建房间")
    @PostMapping("/create")
    public R createRoom(@RequestBody IMVideoMeetingModel model){
        videoService.createRoom(model);
        return R.ok();
    }

    @Operation(summary = "进入房间",description = "进入房间")
    @PostMapping("/enter")
    public R enterRoom(@RequestBody IMVideoMeetingModel model){
        videoService.enterRoom(model);
        return R.ok();
    }

    @Operation(summary = "移除房间成员",description = "移除房间成员")
    @DeleteMapping("/member")
    public R delRoomMember(@RequestBody DelRoomMemberModel model) {
        videoService.delRoomMember(model.getRoomId(), model.getAccountId());
        return R.ok();
    }


    @Operation(summary = "获取房间信息",description = "获取房间信息")
    @GetMapping("/{id}")
    public R<Room> getRoom(@PathVariable("id") String id){
        return R.ok(videoService.getRoom(id));
    }


    @Operation(summary = "删除房间",description = "删除房间")
    @DeleteMapping("/{id}")
    public R delRoom(@PathVariable("id") String id){
        videoService.delRoom(id);
        return R.ok();
    }

    @Operation(summary = "查询录屏文件下载地址",description = "查询录屏文件下载地址")
    @GetMapping("/file/{roomId}")
    public R getRoomRecFile(@PathVariable("roomId") String roomId){
        return R.ok(videoService.getVodDownUrl(roomId));
    }


    @Operation(summary = "删除录屏文件",description = "删除录屏文件")
    @DeleteMapping("/file/{id}")
    public R delRoomRecFile(@PathVariable("id") String id){
        videoService.delVodFile(id);
        return R.ok();
    }

    @PostMapping("/createCloudRecord")
    @Operation(summary = "开启云端录制",description = "开启云端录制")
    public R createCloudRecording(@RequestBody ImCreateCloudRecordModel model){
        videoService.createCloudRecording(model);
        return R.ok();
    }

}
