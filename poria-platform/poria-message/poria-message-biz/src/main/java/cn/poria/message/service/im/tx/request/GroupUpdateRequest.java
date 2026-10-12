package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class GroupUpdateRequest {

    @Alias("GroupId")
    private String groupId;

    @Alias("Name")
    private String name;

    @Alias("Introduction")
    private String introduction;

    @Alias("Notification")
    private String notification;

    @Alias("FaceUrl")
    private String faceUrl;

    /***
     * 最大群成员数量
     */
    @Alias("MaxMemberNum")
    private Integer maxMemberNum;

    /**
     * 申请加群处理方式。包含 FreeAccess（自由加入），NeedPermission（需要验证），DisableApply（禁止加群）。社群目前不支持此字段
     */
    @Alias("ApplyJoinOption")
    private String applyJoinOption;

    /**
     * 默认情况是没有的。开通群组维度的自定义字段
     */
    @Alias("AppDefinedData")
    private List<String> appDefinedData;

    /**
     * 群内群成员禁言，只有群管理员和群主以及系统管理员可以发言
     */
    @Alias("MuteAllMember")
    private String muteAllMember;
}
