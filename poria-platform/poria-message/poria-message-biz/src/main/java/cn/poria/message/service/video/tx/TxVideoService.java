package cn.poria.message.service.video.tx;

import cn.hutool.json.JSONObject;
import cn.poria.message.constant.InformMessageConstant;
import cn.poria.message.service.video.tx.bean.Room;
import cn.poria.message.service.video.VideoService;
import cn.poria.message.service.video.tx.service.TxTRTCService;
import cn.poria.message.service.video.tx.service.TxVideoCallBackService;
import cn.poria.message.service.video.tx.service.TxVodService;
import cn.poria.message.vo.request.im.ImCreateCloudRecordModel;
import cn.poria.message.vo.request.trtc.IMVideoMeetingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TxVideoService implements VideoService {

    @Autowired
    private TxVodService txVodService;

    @Autowired
    private TxTRTCService txTRTCService;

    @Autowired
    private TxVideoCallBackService txVideoCallBackService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void createRoom(IMVideoMeetingModel model) {
        txTRTCService.createRoom(model);
    }

    @Override
    public Room getRoom(String roomId) {
        return txTRTCService.getRoom(roomId);
    }

    @Override
    public void enterRoom(IMVideoMeetingModel model) {
        txTRTCService.enterRoom(model);
    }

    @Override
    public void delRoom(String roomId) {
        txTRTCService.delRoom(roomId);
    }

    @Override
    public void delRoomMember(String roomId,String accountId) {
        txTRTCService.delRoomMember(roomId,accountId);
    }

    @Override
    public void callBackMsgResolve(JSONObject body) {
        txVideoCallBackService.callBackMsgResolve(body);
    }

    @Override
    public String getVodDownUrl(String roomId) {
        return txVodService.getVodDownUrl(roomId);
    }

    @Override
    public void delVodFile(String fileId) {
        txVodService.delVodFile(fileId);
    }

    @Override
    public void createCloudRecording(ImCreateCloudRecordModel model) {
        // 如果 roomId存在，表示已经开始录制；结束时在各个监听的业务中将key移除
        String redisKey = InformMessageConstant.TRTC_VIDEO + model.getRoomId();
        if (redisTemplate.opsForValue().get(redisKey) == null){
            redisTemplate.opsForValue().set(redisKey, model.getRoomId());
            txTRTCService.createCloudRecording(model);
        }
    }

    @Override
    public void videoResultResolve(JSONObject body) {
        txVideoCallBackService.videoResultResolve(body);
    }
}
