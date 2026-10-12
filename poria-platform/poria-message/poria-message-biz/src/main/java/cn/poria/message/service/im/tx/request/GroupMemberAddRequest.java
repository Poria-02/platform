package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import cn.poria.message.service.im.tx.bean.NewMember;
import lombok.Data;

import java.util.List;

@Data
public class GroupMemberAddRequest {

    @Alias("GroupId")
    private String groupId;

    /**
     * 是否静默加人。0：非静默加人；1：静默加人。不填该字段默认为0
     */
    @Alias("Silence")
    private Integer silence;

    /**]]
     * 待添加的群成员数组
     * role为member
     */
    @Alias("MemberList")
    private List<NewMember> memberList;
}
