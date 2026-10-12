package cn.poria.message.vo.response.im;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupInfoVo {

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
    private GroupMemberVo owner;

    @Schema(description = "管理员")
    private List<GroupMemberVo> admins;

    @Schema(description = "群员")
    private List<GroupMemberVo> members;
}
