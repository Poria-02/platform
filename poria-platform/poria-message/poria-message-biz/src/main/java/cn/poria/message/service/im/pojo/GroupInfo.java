package cn.poria.message.service.im.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.hutool.core.collection.CollUtil;
import cn.poria.message.service.im.tx.response.GroupInfoResponse;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupInfo {

    private String groupId;

    @Schema(description = "群名称")
    private String name;

    @Schema(description = "群头像")
    private String faceUrl;

    @Schema(description = "最大群成员数量")
    private Integer maxMemberNum;

    @Schema(description = "群简介")
    private String introduction;

    @Schema(description = "群公告")
    private String notification;

    @Schema(description = "入群方式")
    private String joinOption;

    @Schema(description = "群自定义信息")
    private String customer;

    @Schema(description = "群类型")
    private String type;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "群主")
    private GroupMember owner;

    @Schema(description = "管理员")
    private List<GroupMember> admins;

    @Schema(description = "群员")
    private List<GroupMember> members;


    public void fillMemberInfo(List<GroupInfoResponse.Member> members){

        if(CollUtil.isEmpty(members)){
            return;
        }

        List<GroupInfoResponse.Member> owners = members.stream()
                .filter(e -> e.getRole().equals("Owner"))
                .collect(Collectors.toList());
        if(CollUtil.isNotEmpty(owners)){
            GroupInfoResponse.Member e = owners.get(0);
            GroupMember owner = new GroupMember();
            owner.setAccountId(e.getMemberAccountId());
            owner.setJoinTime(e.getJoinTime());
            this.setOwner(owner);
        }


        List<GroupInfoResponse.Member> admins = members.stream()
                .filter(e -> e.getRole().equals("Admin"))
                .collect(Collectors.toList());
        if(CollUtil.isNotEmpty(admins)){
            this.setAdmins(
                    admins.stream().map(e -> {
                        GroupMember admin = new GroupMember();
                        admin.setAccountId(e.getMemberAccountId());
                        admin.setJoinTime(e.getJoinTime());
                        return admin;
                    }).collect(Collectors.toList())

            );
        }

        List<GroupInfoResponse.Member> commons = members.stream()
                .filter(e -> e.getRole().equals("Member"))
                .collect(Collectors.toList());
        if(CollUtil.isNotEmpty(commons)){
            this.setMembers(
                    commons.stream().map(e -> {
                        GroupMember member = new GroupMember();
                        member.setAccountId(e.getMemberAccountId());
                        member.setJoinTime(e.getJoinTime());
                        return member;
                    }).collect(Collectors.toList())

            );
        }
    }
}
