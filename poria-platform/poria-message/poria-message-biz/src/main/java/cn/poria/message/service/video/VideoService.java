package cn.poria.message.service.video;

import cn.hutool.json.JSONObject;
import cn.poria.message.service.video.tx.bean.Room;
import cn.poria.message.vo.request.im.ImCreateCloudRecordModel;
import cn.poria.message.vo.request.trtc.IMVideoMeetingModel;

public interface VideoService {

    //创建房间
    void createRoom(IMVideoMeetingModel model);

    //获取房间信息
    Room getRoom(String roomId);

    //进入房间
    void enterRoom(IMVideoMeetingModel model);

    //删除房间
    void delRoom(String roomId);

    //删除房间成员
    void delRoomMember(String roomId,String accountId);

    //处理video回调
    void callBackMsgResolve(JSONObject body);

    //获取录屏文件下载地址
    String getVodDownUrl(String roomId);

    //删除文件
    void delVodFile(String fileId);

    void createCloudRecording(ImCreateCloudRecordModel model);

    void videoResultResolve(JSONObject body);
}
