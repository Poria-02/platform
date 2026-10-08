package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户端信息")
public class SysOauthClientDetails extends Model<SysOauthClientDetails> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "客户端id")
    @NotBlank(message = "客户端id不能为空")
    private String clientId;

    @Schema(description = "客户端密钥")
    @NotBlank(message = "客户端密钥不能为空")
    private String clientSecret;

    @Schema(description = "资源id列表")
    private String resourceIds;

    @Schema(description = "作用域")
    @NotBlank(message = "作用域不能为空")
    private String scope;

    @Schema(description = "授权方式")
    private String authorizedGrantTypes;

    @Schema(description = "回调地址")
    private String webServerRedirectUri;

    @Schema(description = "权限列表")
    private String authorities;

    @Schema(description = "请求令牌有效时间")
    private Integer accessTokenValidity;

    @Schema(description = "刷新令牌有效时间")
    private Integer refreshTokenValidity;

    @Schema(description = "扩展信息")
    private String additionalInformation;

    @Schema(description = "是否自动放行")
    private String autoapprove;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;

    @Schema(description = "支付秘钥")
    private String paySecret;
}
