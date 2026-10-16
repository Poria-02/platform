package cn.poria.upms.api.vo.response.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 第三方账号查询结果，不包含应用密钥
 */
@Data
public class SocialDetailsVo {

    @Schema(description = "账号ID")
    private Integer id;

    @Schema(description = "账号类型")
    private String type;

    @Schema(description = "应用AppId")
    private String appId;

    @Schema(description = "账号描述")
    private String remark;

    @Schema(description = "回调地址")
    private String redirectUrl;

    @Schema(description = "是否已配置应用密钥")
    private Boolean secretConfigured;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
