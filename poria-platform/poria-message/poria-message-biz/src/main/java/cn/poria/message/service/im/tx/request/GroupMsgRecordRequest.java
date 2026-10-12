package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class GroupMsgRecordRequest {


    @Alias("GroupId")
    private String groupId;

    /**]]
     * 	拉取的历史消息的条数，目前一次请求最多返回20条历史消息，所以这里最好小于等于20
     */
    @Alias("ReqMsgNumber")
    private Integer reqMsgNumber;


    /**]]
     * 拉取消息的最大 seq
     */
    @Alias("ReqMsgSeq")
    private Long reqMsgSeq;

    /**
     * 是否带撤回的消息，填1表明需要拉取撤回后的消息；默认不拉取撤回后的消息
     */
    @Alias("withRecalledMsg")
    private Integer withRecalledMsg;

    /**
     * 话题的 ID，若具有此选项表示拉取的是对应话题中的消息，仅支持话题的社群适用此选项
     */
    @Alias("TopicId")
    private String topicId;
}
