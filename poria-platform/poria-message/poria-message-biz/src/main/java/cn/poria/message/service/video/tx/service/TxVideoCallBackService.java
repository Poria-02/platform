package cn.poria.message.service.video.tx.service;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.commom.kafka.KafkaUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.api.RemoteBaseService;
import cn.poria.message.service.DingService;
import cn.poria.message.service.video.VideoService;
import cn.poria.message.vo.request.DingSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TxVideoCallBackService {

    @Autowired
    private RemoteBaseService remoteBaseService;

    @Autowired
    private DingService dingService;

    @Autowired
    @Lazy
    private VideoService videoService;


    /**
     *
     * @param body
     *
     * 腾讯：事件回调服务器在发送消息通知后，5秒内没有收到您的服务器的响应，即认为通知失败。首次通知失败后会立即重试，后续失败会以10秒的间隔继续重试，直到消息存续时间超过1分钟，不再重试
     * 加锁，若恰好执行完成，且腾讯重试调用接口，也会产生多条保存记录；其次json  key不易解析；
     * 现处理结果：将保存视频的功能下放到业务类执行
     */
    //处理实时音视频回调
    public void callBackMsgResolve(JSONObject body) {
        log.info("video callback: {}", body.toString());

        String eventType = body.getStr("EventType");
        switch (eventType) {
            //音视频回调
            case "311":

                Object status = JSONUtil.getByPath(body, "EventInfo.Payload.Status");
                if (ObjectUtil.equal(status, 3)) {
                    ThreadUtil.execAsync(() -> {
                        String error = "音视频录屏异常" + body.toString();
                        Map param = new HashMap();
                        param.put("error", error);
                        dingService.sendDingText(DingSend.builder().key("SYSTEM_ERROR").param(param).build());
                    });
                    return;
                }
                KafkaUtil.send("im_tencent_video_event_311", body.toString());

//                //获取文件链接
//                Object url = JSONUtil.getByPath(body, "EventInfo.Payload.TencentVod.VideoUrl");
//
//                R result = remoteBaseService.privateTransferFile(url.toString(), "message/video", null, null, SecurityConstants.FROM_IN);
//                if (result.isSuccess()) {
//                    //广播
//                    String roomId = JSONUtil.getByPath(body, "EventInfo.RoomId").toString();
//                    KafkaUtil.send("im_tencent_video_event_311",
//                            JSONUtil.createObj().putOnce("roomId", roomId)
//                                    .putOnce("fileId", result.getData())
//                                    .putOnce("StartTimeStamp",JSONUtil.getByPath(body, "EventInfo.Payload.TencentVod.StartTimeStamp"))
//                                    .putOnce("EndTimeStamp",JSONUtil.getByPath(body, "EventInfo.Payload.TencentVod.EndTimeStamp")).toString());
//                } else {
//                    log.error("[video callback]: {}", result.getMsg());
//                    return;
//                }
//
//                //删除文件
//                String fileId = JSONUtil.getByPath(body, "EventInfo.Payload.TencentVod.FileId").toString();
//                videoService.delVodFile(fileId);
                break;
            default:
                break;
        }
    }

    /**
     * 通话结果回调
     *
     * @param body
     */
    public void videoResultResolve(JSONObject body) {
        log.info("video result: {}", body.toString());
        KafkaUtil.send("im_tencent_video_result", body.toString());
    }
}
