package cn.poria.message.integration.customer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchunlei
 * @date 2021年06月22日 9:02 上午
 */
@Data
public class UserAccountBindVo {

    /**
     * 机构id
     */

    @Schema(description = "机构id")
    private String orgId;

    /**
     * 帐号编号
     */

    @Schema(description = "帐号编号")
    private String accountId;

    /**
     * 第三方类型，0公众号
     */

    @Schema(description = "第三方类型，0公众号")
    private Integer thirdType;

    /**
     * 第三方唯一标示
     */

    @Schema(description = "第三方唯一标示")
    private String openId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * AppId
     */
    @Schema(description = "AppId")
    private String appId;

    /**
     * 订阅状态，0未关注，1已关注
     */
    @Schema(description = "订阅状态: 0-未关注 1-已关注")
    private String subscribeStatus;

}
