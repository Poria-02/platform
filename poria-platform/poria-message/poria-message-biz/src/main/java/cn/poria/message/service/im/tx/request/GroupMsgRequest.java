package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class GroupMsgRequest {

    @Alias("GroupId")
    private String groupId;

    @Alias("From_Account")
    private String from;

    @Alias("To_Account")
    private List<String> to;

    @Alias("Random")
    private long random;

    /**
     * 消息的优先级
     */
    @Alias("MsgPriority")
    private String msgPriority;

    @Alias("MsgBody")
    private Object msgBody;

    /**
     * 离线推送信息配置
     */
    @Alias("OfflinePushInfo")
    private Object offlinePushInfo;

    /**
     * 消息回调禁止开关，只对单条消息有效，ForbidBeforeSendMsgCallback 表示禁止发消息前回调，ForbidAfterSendMsgCallback 表示禁止发消息后回调
     */
    @Alias("ForbidCallbackControl")
    private List<String> forbidCallbackControl;

    /**
     * 1表示消息仅发送在线成员，默认0表示发送所有成员，AVChatRoom(直播群)不支持该参数
     */
    @Alias("OnlineOnlyFlag")
    private Integer onlineOnlyFlag;

    /**
     * 消息发送权限，NoLastMsg 只对单条消息有效，表示不更新最近联系人会话；NoUnread 不计未读，只对单条消息有效。（如果该消息 OnlineOnlyFlag 设置为1，则不允许使用该字段。）
     */
    @Alias("SendMsgControl")
    private List<String> SendMsgControl;

    /**
     * 消息自定义数据（云端保存，会发送到对端，程序卸载重装后还能拉取到）
     */
    @Alias("CloudCustomData")
    private String cloudCustomData;

    /**
     * 该条消息是否支持消息扩展，0为不支持，1为支持。
     */
    @Alias("supportMessageExtension")
    private Integer supportMessageExtension;

    /**
     * 话题的 ID，若具有此选项表示在对应的话题中发送普通消息，仅支持话题的社群适用此选项
     */
    @Alias("TopicId")
    private String topicId;
}
