package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class GroupMemberDelRequest {


    /**]]
     *
     */
    @Alias("GroupId")
    private String groupId;

    /**
     * 是否静默删人。0表示非静默删人，1表示静默删人。静默即删除成员时不通知群里所有成员，只通知被删除群成员。不填写该字段时默认为0
     */
    @Alias("Silence")
    private Integer silence;

    /**
     * 踢出用户原因
     */
    @Alias("Reason")
    private String reason;

    /**]]
     * 待删除的群成员
     */
    @Alias("MemberToDel_Account")
    private List<String> memberToDelAccount;
}
