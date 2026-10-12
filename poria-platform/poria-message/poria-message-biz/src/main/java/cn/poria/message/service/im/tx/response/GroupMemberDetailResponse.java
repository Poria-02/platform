package cn.poria.message.service.im.tx.response;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class GroupMemberDetailResponse {

    @Alias("JoinTime")
    private Long joinTime;

    @Alias("LastSendMsgTime")
    private Long lastSendMsgTime;

    @Alias("Member_Account")
    private String accountId;

    //成员消息屏蔽类型
    @Alias("MsgFlag")
    private String msgFlag;

    //成员已读消息 seq
    @Alias("MsgSeq")
    private String msgSeq;

    @Alias("NameCard")
    private String nameCard;

    @Alias("Role")
    private String role;

    //0表示未被禁言，否则为禁言的截止时间戳
    @Alias("MuteUntil")
    private Long muteUntil;

    //群成员自定义字段
    @Alias("AppMemberDefinedData")
    private Object appMemberDefinedData;
}
