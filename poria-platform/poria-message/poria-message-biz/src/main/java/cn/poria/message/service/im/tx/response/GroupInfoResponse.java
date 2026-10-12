package cn.poria.message.service.im.tx.response;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GroupInfoResponse {

    @Alias("GroupId")
    private String groupId;

    @Alias("Type")
    private String type;

    @Alias("Name")
    private String name;

    /**
     * 群组简介
     */
    @Alias("Introduction")
    private String introduction;

    /**
     * 群组公告
     */
    @Alias("Notification")
    private String notification;

    @Alias("FaceUrl")
    private String faceUrl;

    @Alias("Owner_Account")
    private String ownerAccount;

    /**
     * 群组的创建时间
     */
    @Alias("CreateTime")
    private Long createTime;

    /**
     * 群资料的每次变都会增加该值
     */
    @Alias("InfoSeq")
    private Integer infoSeq;

    /**
     * 	群组最后一次信息变更时间
     */
    @Alias("LastInfoTime")
    private Integer lastInfoTime;

    /**
     * 群组内最后发消息的时间
     */
    @Alias("LastMsgTime")
    private Integer lastMsgTime;

    /**
     * 群内下一条消息的 Seq
     */
    @Alias("NextMsgSeq")
    private Integer nextMsgSeq;

    /**
     * 当前成员数量
     */
    @Alias("MemberNum")
    private Integer memberNum;

    /**
     * 最大成员数量
     */
    @Alias("MaxMemberNum")
    private Integer maxMemberNum;

    /**
     * 申请加群选项包括如下几种：
     * DisableApply 表示禁止任何人申请加入
     * NeedPermission 表示需要群主或管理员审批
     * FreeAccess 表示允许无需审批自由加入群组
     */
    @Alias("ApplyJoinOption")
    private String applyJoinOption;


    /**
     * 群成员列表
     */
    @Alias("MemberList")
    private List<Member> memberList;

    @Alias("AppDefinedData")
    private List<Map<String,String>> appDefinedData;

    @Data
    public static class Member {
        @Alias("Member_Account")
        private String memberAccountId;

        @Alias("Role")
        private String role;

        /**
         * 入群时间
         */
        @Alias("JoinTime")
        private Long joinTime;

        /**
         * 该成员当前已读消息 Seq
         */
        @Alias("MsgSeq")
        private String msgSeq;

        /**
         * 	消息接收选项
         */
        @Alias("MsgFlag")
        private String msgFlag;

        /**
         * 最后发送消息的时间
         */
        @Alias("LastSendMsgTime")
        private String lastSendMsgTime;

        @Alias("NameCard")
        private String nameCard;

        /**
         * 禁言截止时间
         */
        @Alias("MuteUntil")
        private Integer muteUntil;

        /**
         * 群成员自定义字段
         */
        @Alias("AppMemberDefinedData")
        private Object appMemberDefinedData;
    }
}
