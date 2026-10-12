package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import cn.poria.message.service.im.tx.bean.NewMember;
import lombok.Data;

import java.util.List;

@Data
public class GroupMsgCreateRequest {
    /**
     * 群主 ID（需是 已导入 的帐号）
     */
    @Alias("Owner_Account")
    private String ownerAccountId;

    /**]]
     * 群组形态，包括 Public（陌生人社交群），Private（即 Work，好友工作群），ChatRoom（即 Meeting，会议群），AVChatRoom（直播群），Community（社群）
     */
    @Alias("Type")
    private String type;

    /**
     * 为了使得群组 ID 更加简单，便于记忆传播，腾讯云支持 App 在通过 REST API 创建群组时 自定义群组 ID
     */
    @Alias("GroupId")
    private String groupId;

    /**]]
     * 群名称，最长30字节，使用 UTF-8 编码，1个汉字占3个字节
     */
    @Alias("Name")
    private String name;

    /**
     *群简介
     */
    @Alias("Introduction")
    private String introduction;

    /**
     * 群公告
     */
    @Alias("Notification")
    private String notification;

    /**
     * 群头像
     */
    @Alias("FaceUrl")
    private String FaceUrl;

    /**
     * 	最大群成员数量
     */
    @Alias("MaxMemberCount")
    private Integer maxMemberCount;

    /**
     * 申请加群处理方式。包含 FreeAccess（自由加入），NeedPermission（需要验证），DisableApply（禁止加群），不填默认为 NeedPermission（需要验证）
     */
    @Alias("ApplyJoinOption")
    private String applyJoinOption;

    /**
     * 初始群成员列表，最多100个
     */
    @Alias("MemberList")
    private List<NewMember> memberList;


    @Alias("AppDefinedData")
    private Object appDefinedData;


    /***
     * 是否支持话题选项，此字段只针对社群有效
     */
    @Alias("SupportTopic")
    private Integer supportTopic;
}
