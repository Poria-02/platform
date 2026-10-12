package cn.poria.message.service.im.tx.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.enums.txim.TxMsgUrlConstant;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.service.im.tx.request.RoomCreateRequest;
import cn.poria.message.service.im.tx.response.RoomInfoResponse;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.vo.request.im.CreateRoomModel;
import cn.poria.message.vo.request.im.DestroyRoomModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * author qd
 * date 2024/12/10 17:48
 * description 视频管理
 */
@Component
public class TxIMRoomService {

    @Resource
    private TxIMProperties txIMProperties;

    @Resource
    private TxIMMsgSendUtil txIMMsgSendUtil;


    public void createRoom(CreateRoomModel model) {

        try {
            if(StrUtil.isNotBlank(model.getRoomId()) && model.getRoomId().getBytes(StandardCharsets.UTF_8).length > 48){
                throw new ServiceException("房间id超过48字节");
            }

            if(StrUtil.isNotBlank(model.getRoomName()) && model.getRoomName().getBytes(StandardCharsets.UTF_8).length > 100){
                throw new ServiceException("房间名称超过240字节");
            }

            if(StrUtil.isNotBlank(model.getCustomInfo()) && model.getCustomInfo().getBytes(StandardCharsets.UTF_8).length > 500){
                throw new ServiceException("房间自定义消息超过500字节");
            }

        }catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        RoomCreateRequest createRequest = new RoomCreateRequest();
        createRequest.setRoomId(model.getRoomId());
        createRequest.setRoomName(model.getRoomName());
        createRequest.setRoomType("Conference");
        createRequest.setScheduleStartTime(model.getScheduleStartTime());
        createRequest.setScheduleEndTime(model.getScheduleEndTime());
        createRequest.setCustomInfo(model.getCustomInfo());
        createRequest.setOwnerAccountId(model.getOwnerAccountId());
        createRequest.setIsCloudRecordingDisabled(true);//开启云录制
        JSONObject json = new JSONObject();
        json.set("RoomInfo", createRequest);

        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ROOM_CREATE, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(json));
    }

    public void destroyRoom(DestroyRoomModel model) {
        JSONObject json = new JSONObject();
        json.set("RoomId", model.getRoomId());
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ROOM_DESTROY, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(json));
    }

    public RoomInfoResponse getRoom(String roomId) {
        JSONObject json = new JSONObject();
        json.set("RoomId", roomId);

        JSONObject response = txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ROOM_GET_INFO, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(json));

        if (Integer.parseInt(response.get("ErrorCode").toString()) == 0){
            return JSONUtil.parseObj(response.get("Response")).toBean(RoomInfoResponse.class);
        }
        return null;
    }
}
