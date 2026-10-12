package cn.poria.message.service.video.tx.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Room implements Serializable {

    private static final long serialVersionUID = 3841022287879021079L;

    @Schema(description = "房间id")
    private String roomId;

    @Schema(description = "房间名称")
    private String roomName;

    @Schema(description = "会议最大人数")
    private String maxSize;

    @Schema(description = "房间类型 0-视频房间")
    private Integer type;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "扩展信息")
    private String extContent;

    @Schema(description = "房间成员")
    private ConcurrentHashMap<String, RoomMember> membersMap = new ConcurrentHashMap<>();

    /**
     * 获取房间成员数
     */
    @JsonIgnore
    public int getRoomMemberCnt(){
        return membersMap.size();
    }

    /**
     * 添加成员
     */
    @JsonIgnore
    public void addMember(String accountId,String nickName,String role){
        RoomMember member = new RoomMember();
        member.setAccountId(accountId);
        member.setRole(role);
        member.setNickName(nickName);
        member.setJoinTime(new Date());
        this.membersMap.put(accountId,member);
    }

    /**
     * 判断群组是否包含成员
     */
    @JsonIgnore
    public boolean isMember(String accountId){
        return this.membersMap.containsKey(accountId);
    }

    /**
     * 删除群成员
     */
    @JsonIgnore
    public void delMember(String accountId){
        this.membersMap.remove(accountId);
    }


}
