package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import cn.hutool.json.JSONArray;
import lombok.Data;

import java.util.List;

@Data
public class GroupMemberInfoUpdateRequest {

    /**]]
     *
     */
    @Alias("GroupId")
    private String groupId;

    /**]]
     * 要操作的群成员
     */
    @Alias("Member_Account")
    private String memberAccount;

    /**
     * 成员身份，Admin/Member 分别为设置/取消管理员
     */
    @Alias("Role")
    private String role;

    /**
     * 消息屏蔽类型
     */
    @Alias("MsgFlag")
    private String msgFlag;

    /**
     * 群名片（最大不超过50个字节）
     */
    @Alias("NameCard")
    private String nameCard;

    /**
     * 群成员维度的自定义字段，默认情况是没有的
     */
    @Alias("AppMemberDefinedData")
    private JSONArray appMemberDefinedData;

    /**
     * 需禁言时间，单位为秒，0表示取消禁言
     */
    @Alias("MuteTime")
    private Integer muteTime;
}
