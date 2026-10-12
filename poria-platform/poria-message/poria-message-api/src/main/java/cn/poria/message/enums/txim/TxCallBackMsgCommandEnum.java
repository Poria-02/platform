package cn.poria.message.enums.txim;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum TxCallBackMsgCommandEnum {

            //在线状态
            STATUS_CHANGE("State.StateChange","状态变更回调"),

            //资料关系链
            PROFILE_CHANGE_AFTER("Profile.CallbackPortraitSet","更新资料之后回调"),
            ADD_FRIEND_BEFORE("Sns.CallbackPrevFriendAdd","添加好友之前回调"),
            ADD_FRIEND_BACK_AFTER("Sns.CallbackPrevFriendResponse","添加好友回应之前回调"),
            ADD_FRIEND_AFTER("Sns.CallbackFriendAdd","添加好友之后回调"),
            DEL_FRIEND_AFTER("Sns.CallbackFriendDelete","删除好友之后回调"),
            ADD_BLOCK_AFTER("Sns.CallbackBlackListAdd","添加黑名单之后回调"),
            DEL_BLOCK_DEL("Sns.CallbackBlackListDelete","删除黑名单之后回调"),

            //单聊消息
            SEND_P2P_MSG_BEFORE("C2C.CallbackBeforeSendMsg","发单聊消息之前回调"),//  TODO 已完成
            SEND_P2P_MSG_AFTER("C2C.CallbackAfterSendMsg","发单聊消息之后回调"),
            P2P_MSG_REPORT_AFTER("C2C.CallbackAfterMsgReport","单聊消息已读上报后回调"),
            P2P_MSG_BACK_AFTER("C2C.CallbackAfterMsgWithDraw","单聊消息撤回后回调"),//  TODO 已完成

            //群组系统
            GROUP_CREATE_BEFORE("Group.CallbackBeforeCreateGroup","创建群组之前回调"), // TODO 已完成
            GROUP_CREATE_AFTER("Group.CallbackAfterCreateGroup","创建群组之后回调"),
            GROUP_APPLY_BEFORE("Group.CallbackBeforeApplyJoinGroup","申请入群之前回调"),
            GROUP_INVITE_BEFORE("Group.CallbackBeforeInviteJoinGroup","拉人入群之前回调"),
            NEW_MEMBER_ADD_AFTER("Group.CallbackAfterNewMemberJoin","新成员入群之后回调"), //  TODO 已完成
            MEMBER_OUT_AFTER("Group.CallbackAfterMemberExit","群成员离开之后回调"),//  TODO 已完成
            SEND_GROUP_MSG_BEFORE("Group.CallbackBeforeSendMsg","群内发言之前回调"),
            SEND_GROUP_MSG_AFTER("Group.CallbackAfterSendMsg","群内发言之后回调"),//  TODO 已完成
            GROUP_FULL_AFTER("Group.CallbackAfterGroupFull","群组满员之后回调"),
            GROUP_DESTROY_AFTER("Group.CallbackAfterGroupDestroyed","群组解散之后回调"),
            GROUP_PROFILE_CHANGE_AFTER("Group.CallbackAfterGroupInfoChanged","群组资料变动之后回调"), // TODO 已完成
            LIVE_GROUP_ONLINE_STATUS("Group.CallbackOnMemberStateChange","直播群成员在线状态回调"),
            SEND_GROUP_MSG_ERROR("Group.CallbackSendMsgException","发送群聊消息异常回调"),
            CREATE_TOPIC_BEFORE("Group.CallbackBeforeCreateTopic","创建话题之前回调"),
            CREATE_TOPIC_AFTER("Group.CallbackAfterCreateTopic","创建话题之后回调"),
            DESTROY_TOPIC_AFTER("Group.CallbackAfterTopicDestroyed","解散话题之后回调"),
            CHANGE_TOPIC_AFTER("Group.CallbackAfterTopicInfoChanged","话题资料修改之后回调"),
            GROUP_RECALL_AFTER("Group.CallbackAfterRecallMsg","撤回群消息之后回调"), //  TODO 已完成
    ;


    private String code;

    private String desc;

    private static final Map<String,TxCallBackMsgCommandEnum> maps = Arrays.stream(values()).collect(Collectors.toMap(TxCallBackMsgCommandEnum::getCode,o -> o));

    TxCallBackMsgCommandEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TxCallBackMsgCommandEnum getEnum(String code) {
        return maps.get(code);
    }

}
