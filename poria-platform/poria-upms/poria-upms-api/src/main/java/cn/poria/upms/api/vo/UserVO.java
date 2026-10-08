package cn.poria.upms.api.vo;

import cn.poria.common.core.sensitive.Sensitive;
import cn.poria.common.core.sensitive.SensitiveTypeEnum;
import cn.poria.upms.api.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "前端用户展示对象")
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "随机盐")
    private String salt;

    @Schema(description = "微信open id")
    private String wxOpenid;

    @Schema(description = "qq open id")
    private String qqOpenid;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "锁定标记,0:正常,9:已锁定")
    private String lockFlag;

    @Sensitive(type = SensitiveTypeEnum.MOBILE_PHONE)
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "所属部门")
    private Integer deptId;

    @Schema(description = "所属租户")
    private Integer tenantId;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "管理端最后登陆时间")
    private String lastLoginTimeManager;

    @Schema(description = "运营端最后登陆时间")
    private String lastLoginTimeOperate;

    @Schema(description = "拥有的角色列表")
    private List<SysRole> roleList;
}
