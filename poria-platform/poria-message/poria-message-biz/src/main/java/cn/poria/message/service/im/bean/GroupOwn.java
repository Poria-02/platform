package cn.poria.message.service.im.bean;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class GroupOwn {

    @Alias("GroupId")
    private String groupId;

    @Alias("Introduction")
    private String introduction;

    @Alias("Name")
    private String name;

    @Alias("Notification")
    private String notification;

    @Alias("Owner_Account")
    private String ownerAccountId;

    @Alias("Type")
    private String type;

    @Alias("SelfInfo")
    private GroupSelfInfo selfInfo;

    @Data
    public static class GroupSelfInfo{

        @Alias("Role")
        private String role;

        @Alias("JoinTime")
        private long joinTime;
    }
}
