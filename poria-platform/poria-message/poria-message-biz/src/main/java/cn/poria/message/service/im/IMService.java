package cn.poria.message.service.im;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.poria.message.service.im.bean.GroupOwn;
import cn.poria.message.service.im.pojo.GroupInfo;
import cn.poria.message.service.im.tx.bean.AccountInfo;
import cn.poria.message.service.im.tx.request.GroupOwnRequest;
import cn.poria.message.vo.request.im.*;
import cn.poria.message.vo.response.im.GroupInfoVo;
import cn.poria.message.vo.response.im.RoomInfoVo;

import java.util.List;
import java.util.Map;

public interface IMService {

    //======================[Account]=========================//
    //创建账户
    String createAccount(IMRegister imRegister);

    //查询账号信息
    Map<String, AccountInfo> findAccountInfo(List<String> accountIds);

    //更新账户信息
    void updateAccount(IMModifyAccountModel model);

    //删除账号
    void deleteAccount(List<String> accountId);



    //======================[P2P]=========================//
    //发送p2p消息
    void sendP2PMsg(IMMsgSend imMsgSend);

    //查询p2p聊天记录
    JSONArray findP2PMsgHistory(ChatLogModel model);

    //服务器消息推送
    void pushServerMsg(IMPush imPush);



    //======================[Group]=========================//

    //创建群聊
    String createGroup(CreateGroupModel model);

    //发送团队消息
    void sendGroupMsg(IMMsgSend imMsgSend);

    //邀请群成员
    void addGroupMember(AddGroupPersonModel model);

    void addGroupMemberExt(AddMemberExtModel model);

    //踢人
    void removeGroupMember(DelGroupMemberModel model);

    //查询自己的群组信息
    List<GroupOwn> ownGroup(GroupOwnRequest groupOwnRequest);

    //解散群
    void destroyGroup(DestroyGroupModel model);

    //查询多个群信息
    List<GroupInfo> listGroupInfo(List<String> groupIds);

    //查询单个群信息
    GroupInfoVo info(String groupId);

    //查询群聊天记录
    JSONArray findGroupHistory(GroupChatLogModel model);

    //更新群信息
    void updateGroupInfo(GroupNameModifyModel model);

    //更新群员信息
    void updateGroupMemberInfo(GroupMemberNickModel model);

    //查询群成员详细信息
    List<GroupMemberDetail> getMemberDetail(String groupId);

    //回调处理
    void callBackMsgResolve(JSONObject body);

    void createRoom(CreateRoomModel model);

    void destroyRoom(DestroyRoomModel roomId);

    RoomInfoVo getRoom(String roomId);

    void transferGroup(TransferGroupModel model);
}
