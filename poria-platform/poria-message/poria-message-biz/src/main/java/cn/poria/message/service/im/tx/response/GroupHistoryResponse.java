package cn.poria.message.service.im.tx.response;

import cn.hutool.core.annotation.Alias;
import lombok.Data;


@Data
public class GroupHistoryResponse {

    @Alias("GroupId")
    private String groupId;

    @Alias("From_Account")
    private String from;

    @Alias("MsgBody")
    private Object msgBody;

    @Alias("MsgPriority")
    private Integer msgPriority;

    @Alias("MsgRandom")
    private Integer msgRandom;

    @Alias("MsgSeq")
    private Integer msgSeq;

    @Alias("MsgTimeStamp")
    private Integer msgTimeStamp;

}
