package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class GroupOwnRequest {

    /**]]
     * 需要查询的用户帐号
     */
    @Alias("Member_Account")
    private String accountId;

    /**
     * 是否获取用户加入的 AVChatRoom(直播群)，0表示不获取，1表示获取。默认为0
     */
    @Alias("WithHugeGroups")
    private Integer withHugeGroups;

    /**
     * 是否获取用户已加入但未激活的 Private（即新版本中 Work，好友工作群) 群信息，0表示不获取，1表示获取。默认为0
     */
    @Alias("WithNoActiveGroups")
    private Integer withNoActiveGroups;

    /**
     * 	单次拉取的群组数量，如果不填代表所有群组
     */
    @Alias("Limit")
    private Integer limit;

    /**
     * 从第多少个群组开始拉取
     */
    @Alias("Offset")
    private Integer offset;

    /**
     * 拉取哪种群组类型，例如 Public(陌生人社交群)，Private（即新版本Work，好友工作群)，ChatRoom （即新版本Meeting，会议群），AVChatRoom(直播群)，Community（社群），不填为拉取所有
     */
    @Alias("GroupType")
    private String groupType;

    /**
     * 分别包含 GroupBaseInfoFilter 和 SelfInfoFilter 两个过滤器
     */
    @Alias("ResponseFilter")
    private Object responseFilter;

    /**
     *指定查询的是否为支持话题的群组，1表示支持，0表示不支持
     */
    @Alias("SupportTopic")
    private Integer supportTopic;

}
