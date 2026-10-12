package cn.poria.message.vo.request.im;

import lombok.Data;

@Data
public class GroupMemberDetail {

    private Long joinTime;

    private Long lastSendMsgTime;

    private String accountId;

    //成员消息屏蔽类型
    private String msgFlag;

    //成员已读消息 seq
    private String msgSeq;

    private String nickName;

    private String role;

    //0表示未被禁言，否则为禁言的截止时间戳
    private Long muteUntil;

    //群成员自定义字段
    private Object appMemberDefinedData;
}
