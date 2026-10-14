package cn.poria.message.vo.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class TemplateVo implements Serializable {

    // 保持已有 Redis 模板缓存的 Java 序列化兼容性。
    @Serial
    private static final long serialVersionUID = 7005165895354096961L;

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
     *模板类型 1-短信，2-站内信，3-微信公众号，4-小程序
     */

    @Schema(description = "模板类型：1 短信、2 站内信、3 微信公众号、4 小程序", allowableValues = {"1", "2", "3", "4"})
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
     *消息跳转URL，支持模板变量
     */

    @Schema(description = "消息跳转URL，支持模板变量")
    private String url;

    /**
     *短信平台 短信消息传入
     */

    @Schema(description = "短信平台 短信消息传入")
    private String platform;

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
