package cn.poria.message.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 消息模板(MsgTemplate)表实体类
 *
 * @author makejava
 * @since 2020-07-20 16:04:08
 */
@Data
public class MsgTemplate {

    /**
     *id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "ID创建人")
    private String createBy;

    /**
     *创建时间
     */
    @TableField(fill = FieldFill.INSERT)
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
     *是否删除0-没删除 1-删除
     */
    @TableLogic(value = "0",delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "是否删除0-没删除 1-删除")
    private Integer isDelete;

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
