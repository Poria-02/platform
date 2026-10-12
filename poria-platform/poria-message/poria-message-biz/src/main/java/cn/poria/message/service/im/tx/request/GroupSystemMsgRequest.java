package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class GroupSystemMsgRequest {

    @Alias("GroupId")
    private String groupId;

    @Alias("ToMembers_Account")
    private List<String> memberAccounts;

    @Alias("Content")
    private String content;

    @Alias("TopicId")
    private String topicId;
}
