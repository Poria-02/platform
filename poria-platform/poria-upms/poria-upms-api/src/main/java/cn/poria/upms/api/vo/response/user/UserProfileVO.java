package cn.poria.upms.api.vo.response.user;

import cn.poria.common.core.sensitive.Sensitive;
import cn.poria.common.core.sensitive.SensitiveTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "当前登录用户的个人资料")
public class UserProfileVO {
    @Schema(description = "账号编号")
    private Long userId;

    @Schema(description = "账号名称")
    private String username;

    @Sensitive(type = SensitiveTypeEnum.MOBILE_PHONE)
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "账号状态，0正常、9锁定")
    private String lockFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "所属角色名称")
    private List<String> roles;
}
