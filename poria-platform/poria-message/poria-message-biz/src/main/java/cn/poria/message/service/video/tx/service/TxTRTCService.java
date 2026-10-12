package cn.poria.message.service.video.tx.service;

import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.enums.trtc.TxTRTCConstant;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.service.video.tx.bean.Room;
import cn.poria.message.service.video.tx.config.TxTRTCProperties;
import cn.poria.message.service.video.tx.util.RedisIMVideoSupportUtil;
import cn.poria.message.vo.request.im.ImCreateCloudRecordModel;
import cn.poria.message.vo.request.trtc.IMVideoMeetingModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.trtc.v20190722.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class TxTRTCService {

    @Autowired
    private TxTRTCProperties txTRTCProperties;

    @Autowired
    private RedisIMVideoSupportUtil redisIMVideoSupportUtil;

    @Lazy
    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;

    @Autowired
    private TrtcClient trtcClient;
    /**
     * 开启云端录制
     */
    public void createCloudRecording(ImCreateCloudRecordModel model){
        try {
            CreateCloudRecordingRequest txRequest = new CreateCloudRecordingRequest();
            txRequest.setRoomId(model.getRoomId());
            txRequest.setRoomIdType(0L);
            txRequest.setSdkAppId(txTRTCProperties.getSdkAppId());

            //设置录制机器人
            txRequest.setUserId(model.getRoomId() + "_1");
            txRequest.setUserSig(txIMMsgSendUtil.getSig(model.getRoomId() + "_1"));

            //设置合流参数
            RecordParams recordParams = new RecordParams();
            recordParams.setRecordMode(2L);//合流录制
            recordParams.setMaxIdleTime(5L);
            txRequest.setRecordParams(recordParams);


            //设置布局参数，type
            MixLayoutParams  mixLayoutParams = new MixLayoutParams();
            if(model.getType() == null || model.getType() == 1){
                mixLayoutParams.setMixLayoutMode(1L);
                mixLayoutParams.setMaxResolutionUserId(model.getMaxResolutionUserId());
            }else if (model.getType() == 3){
                mixLayoutParams.setMixLayoutMode(3L);
            }
            txRequest.setMixLayoutParams(mixLayoutParams);

            //设置存储参数
            StorageParams storageParams = new StorageParams();
            CloudVod cloudVod = new CloudVod();
            TencentVod tencentVod = new TencentVod();
            tencentVod.setExpireTime(86400L);
            tencentVod.setSourceContext(model.getRoomId());
            cloudVod.setTencentVod(tencentVod);


            storageParams.setCloudVod(cloudVod);
            txRequest.setStorageParams(storageParams);
            log.info("请求:{}",JSONUtil.toJsonStr(txRequest));
            CreateCloudRecordingResponse response = trtcClient.CreateCloudRecording(txRequest);
            log.info("开启云端录制返回:{}", JSONUtil.toJsonStr(response));

        }catch (TencentCloudSDKException e){
            log.error("腾讯TRTC服务请求异常:", e);
            throw new ServiceException("腾讯TRTC服务请求异常,错误码: " + e.getErrorCode());
        }

    }

    public void createRoom(IMVideoMeetingModel model){

        Object existsRoom = redisIMVideoSupportUtil.getRoom(TxTRTCConstant.ROOM_CACHE_KEY, model.getRoomId());
        if(existsRoom != null){
            throw new ServiceException("房间已存在请勿重复创建");
        }
        Room room = new Room();
        room.setRoomId(model.getRoomId());
        room.setRoomName(model.getRoomName());
        room.setCreateTime(new Date());
        for (IMVideoMeetingModel.Member member : model.getMembers()) {
            room.addMember(member.getAccountId(), member.getNickName(), member.getRole());
        }
        redisIMVideoSupportUtil.putMeetingMap(TxTRTCConstant.ROOM_CACHE_KEY, model.getRoomId(),room);
    }


    public void enterRoom(IMVideoMeetingModel model){
        Room existRoom = (Room)redisIMVideoSupportUtil.getRoom(TxTRTCConstant.ROOM_CACHE_KEY, model.getRoomId());
        if(existRoom == null){
            throw new ServiceException("房间不存在");
        }
        for (IMVideoMeetingModel.Member member : model.getMembers()) {
            existRoom.addMember(member.getAccountId(), member.getNickName(), member.getNickName());
        }
        redisIMVideoSupportUtil.putMeetingMap(TxTRTCConstant.ROOM_CACHE_KEY, model.getRoomId(),existRoom);
    }

    public void delRoomMember(String roomId,String accountId){
        Room existRoom = (Room)redisIMVideoSupportUtil.getRoom(TxTRTCConstant.ROOM_CACHE_KEY, roomId);
        if(existRoom == null){
            throw new ServiceException("房间不存在");
        }

        if(existRoom.isMember(accountId)){
            Room clone = SerializationUtils.clone(existRoom);
            existRoom.delMember(accountId);
            redisIMVideoSupportUtil.putMeetingMap(TxTRTCConstant.ROOM_CACHE_KEY, roomId,existRoom);

            try {
                RemoveUserRequest txRequest = new RemoveUserRequest();
                txRequest.setRoomId(Long.parseLong(roomId));
                txRequest.setSdkAppId(txTRTCProperties.getSdkAppId());
                txRequest.setUserIds(new String[]{accountId});
                trtcClient.RemoveUser(txRequest);
            } catch (TencentCloudSDKException e) {
                log.info("腾讯TRTC服务请求异常, Action[{}] \n message: {}","移除群成员", JSONUtil.toJsonStr(e));
                redisIMVideoSupportUtil.putMeetingMap(TxTRTCConstant.ROOM_CACHE_KEY, clone.getRoomId(),clone);
                throw new ServiceException("腾讯TRTC服务请求异常,错误码: " + e.getErrorCode());
            }
        }
    }


    public Room getRoom(String roomId){
        return (Room)redisIMVideoSupportUtil.getRoom(TxTRTCConstant.ROOM_CACHE_KEY, roomId);
    }

    public void delRoom(String roomId){
        Room existRoom = (Room)redisIMVideoSupportUtil.getRoom(TxTRTCConstant.ROOM_CACHE_KEY, roomId);
        if(existRoom == null){
            throw new ServiceException("房间不存在");
        }
        redisIMVideoSupportUtil.delMap(TxTRTCConstant.ROOM_CACHE_KEY, roomId);

        try {
            DismissRoomRequest txRequest = new DismissRoomRequest();
            txRequest.setRoomId(Long.parseLong(roomId));
            txRequest.setSdkAppId(txTRTCProperties.getSdkAppId());
            trtcClient.DismissRoom(txRequest);
        } catch (TencentCloudSDKException e) {
            log.info("腾讯TRTC服务请求异常, Action[{}] \n message: {}","解散房间", JSONUtil.toJsonStr(e));
            redisIMVideoSupportUtil.putMeetingMap(TxTRTCConstant.ROOM_CACHE_KEY, existRoom.getRoomId(),existRoom);
            throw new ServiceException("腾讯TRTC服务请求异常,错误码: " + e.getErrorCode());
        }
    }

}
