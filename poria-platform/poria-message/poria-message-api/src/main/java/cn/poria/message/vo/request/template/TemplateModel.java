package cn.poria.message.vo.request.template;

import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class TemplateModel {

    @NotBlank(message = "id不能为空",groups = Update.class)
    @Null(message = "id勿传",groups = Create.class)
    private String id;

    /**
     *模板标题,用于描述
     */

    @NotBlank(message = "标题不能为空")
    @Schema(description = "模板标题,用于描述")
    private String title;

    /**
     *模版KEY 模版使用时传入
     */

    @NotBlank(message = "模版KEY不能为空")
    @Schema(description = "模版KEY 模版使用时传入")
    private String templateKey;

    /**
     *模板内容,需替换变量写在${}中
     */

    @NotBlank(message = "模版内容不能为空")
    @Schema(description = "模板内容,需替换变量写在${}中")
    private String content;

    /**
     *模板类型 1-短信模板，2-消息推送模版，3-站内信
     */

    @NotNull(message = "模版类型不能为空")
    @Schema(description = "模板类型 字典message_type")
    private String type;

    /**
     *停用标志  1;已停用  0:正常
     */

    @Schema(description = "停用标志  1-正常,2-停用")
    private Integer status;

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

    @Schema(description = "通知手机号 钉钉消息时使用")
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
