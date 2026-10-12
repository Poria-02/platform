package cn.poria.message.vo.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TemplateVo implements Serializable {


    /**
     *id
     */
    @Schema(description = "id")
    private String id;

    /**
     *模板标题,用于描述
     */

    @Schema(description = "模板标题,用于描述")
    private String title;

    /**
     *模版KEY 模版使用时传入
     */

    @Schema(description = "模版KEY 模版使用时传入")
    private String templateKey;

    /**
     *模板内容,需替换变量写在${}中
     */

    @Schema(description = "模板内容,需替换变量写在${}中")
    private String content;

    /**
     *ID创建人
     */
    @Schema(description = "ID创建人")
    private String createBy;

    /**
     *创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     *模板类型 1-短信模板，2-消息推送模版，3-站内信
     */

    @Schema(description = "模板类型 1-短信模板，2-消息推送模版，3-站内信")
    private String type;

    /**
     *停用标志  1-正常，2-停用
     */

    @Schema(description = "停用标志  1-正常，2-停用")
    private Integer status;


    /**
     *租户ID
     */

    @Schema(description = "租户ID")
    private Integer tenantId;

    /**
     *钉钉机器人URL，钉钉消息传入
     */

    @Schema(description = "钉钉机器人URL，钉钉消息传入")
    private String url;

    /**
     *是否@all 0-否 1-是钉钉消息使用
     */

    @Schema(description = "是否@all 0-否 1-是钉钉消息使用")
    private Integer isAtAll;

    /**
     *通知手机号 钉钉小时使用
     */

    @Schema(description = "通知手机号 钉钉小时使用")
    private String mobiles;

    /**
     *短信平台 短信消息传入
     */

    @Schema(description = "短信平台 短信消息传入")
    private String platform;

    /**
     *钉钉秘钥
     */

    @Schema(description = "钉钉秘钥")
    private String ddSgin;

    /**
     * 模版分类
     */

    @Schema(description = "模版分类")
    private String category;

    /**
     * 参数映射配置
     */

    @Schema(description = "参数映射配置")
    private String paramMapping;

}
