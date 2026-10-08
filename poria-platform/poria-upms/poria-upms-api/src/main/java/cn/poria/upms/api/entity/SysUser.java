package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @JsonIgnore
    @Schema(description = "随机盐")
    private String salt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "锁定标记")
    private String lockFlag;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "用户所属部门id")
    private Long deptId;

    @Schema(description = "用户所属租户id")
    private Integer tenantId;

    @Schema(description = "微信openid")
    private String wxOpenid;

    @Schema(description = "微信小程序openid")
    private String miniOpenid;

    @Schema(description = "QQ openid")
    private String qqOpenid;

    @Schema(description = "码云唯一标识")
    private String giteeLogin;

    @Schema(description = "开源中国唯一标识")
    private String oscId;

    @Schema(description = "网易云信Token")
    private String yxToken;

    private Date lastPwdUpdateTime;
}
