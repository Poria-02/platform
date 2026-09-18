package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "客户端信息")
public class SysOauthClientDetails extends Model<SysOauthClientDetails> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "客户端id")
    @NotBlank(message = "client_id 不能为空")
    private String clientId;

    @Schema(description = "客户端密钥")
    @NotBlank(message = "client_secret 不能为空")
    private String clientSecret;

    @Schema(description = "资源id列表")
    private String resourceIds;

    @Schema(description = "作用域")
    @NotBlank(message = "scope 不能为空")
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

    public Integer getId() {
        return this.id;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getResourceIds() {
        return this.resourceIds;
    }

    public String getScope() {
        return this.scope;
    }

    public String getAuthorizedGrantTypes() {
        return this.authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return this.webServerRedirectUri;
    }

    public String getAuthorities() {
        return this.authorities;
    }

    public Integer getAccessTokenValidity() {
        return this.accessTokenValidity;
    }

    public Integer getRefreshTokenValidity() {
        return this.refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return this.additionalInformation;
    }

    public String getAutoapprove() {
        return this.autoapprove;
    }

    public String getDelFlag() {
        return this.delFlag;
    }

    public String getPaySecret() {
        return this.paySecret;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setResourceIds(final String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public void setAuthorizedGrantTypes(final String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public void setWebServerRedirectUri(final String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public void setAuthorities(final String authorities) {
        this.authorities = authorities;
    }

    public void setAccessTokenValidity(final Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public void setRefreshTokenValidity(final Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public void setAdditionalInformation(final String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setAutoapprove(final String autoapprove) {
        this.autoapprove = autoapprove;
    }

    public void setDelFlag(final String delFlag) {
        this.delFlag = delFlag;
    }

    public void setPaySecret(final String paySecret) {
        this.paySecret = paySecret;
    }

    public String toString() {
        Integer var10000 = this.getId();
        return "SysOauthClientDetails(id=" + var10000 + ", clientId=" + this.getClientId() + ", clientSecret=" + this.getClientSecret() + ", resourceIds=" + this.getResourceIds() + ", scope=" + this.getScope() + ", authorizedGrantTypes=" + this.getAuthorizedGrantTypes() + ", webServerRedirectUri=" + this.getWebServerRedirectUri() + ", authorities=" + this.getAuthorities() + ", accessTokenValidity=" + this.getAccessTokenValidity() + ", refreshTokenValidity=" + this.getRefreshTokenValidity() + ", additionalInformation=" + this.getAdditionalInformation() + ", autoapprove=" + this.getAutoapprove() + ", delFlag=" + this.getDelFlag() + ", paySecret=" + this.getPaySecret() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SysOauthClientDetails)) {
            return false;
        } else {
            SysOauthClientDetails other = (SysOauthClientDetails) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$accessTokenValidity = this.getAccessTokenValidity();
                Object other$accessTokenValidity = other.getAccessTokenValidity();
                if (this$accessTokenValidity == null) {
                    if (other$accessTokenValidity != null) {
                        return false;
                    }
                } else if (!this$accessTokenValidity.equals(other$accessTokenValidity)) {
                    return false;
                }

                Object this$refreshTokenValidity = this.getRefreshTokenValidity();
                Object other$refreshTokenValidity = other.getRefreshTokenValidity();
                if (this$refreshTokenValidity == null) {
                    if (other$refreshTokenValidity != null) {
                        return false;
                    }
                } else if (!this$refreshTokenValidity.equals(other$refreshTokenValidity)) {
                    return false;
                }

                Object this$clientId = this.getClientId();
                Object other$clientId = other.getClientId();
                if (this$clientId == null) {
                    if (other$clientId != null) {
                        return false;
                    }
                } else if (!this$clientId.equals(other$clientId)) {
                    return false;
                }

                Object this$clientSecret = this.getClientSecret();
                Object other$clientSecret = other.getClientSecret();
                if (this$clientSecret == null) {
                    if (other$clientSecret != null) {
                        return false;
                    }
                } else if (!this$clientSecret.equals(other$clientSecret)) {
                    return false;
                }

                Object this$resourceIds = this.getResourceIds();
                Object other$resourceIds = other.getResourceIds();
                if (this$resourceIds == null) {
                    if (other$resourceIds != null) {
                        return false;
                    }
                } else if (!this$resourceIds.equals(other$resourceIds)) {
                    return false;
                }

                Object this$scope = this.getScope();
                Object other$scope = other.getScope();
                if (this$scope == null) {
                    if (other$scope != null) {
                        return false;
                    }
                } else if (!this$scope.equals(other$scope)) {
                    return false;
                }

                Object this$authorizedGrantTypes = this.getAuthorizedGrantTypes();
                Object other$authorizedGrantTypes = other.getAuthorizedGrantTypes();
                if (this$authorizedGrantTypes == null) {
                    if (other$authorizedGrantTypes != null) {
                        return false;
                    }
                } else if (!this$authorizedGrantTypes.equals(other$authorizedGrantTypes)) {
                    return false;
                }

                Object this$webServerRedirectUri = this.getWebServerRedirectUri();
                Object other$webServerRedirectUri = other.getWebServerRedirectUri();
                if (this$webServerRedirectUri == null) {
                    if (other$webServerRedirectUri != null) {
                        return false;
                    }
                } else if (!this$webServerRedirectUri.equals(other$webServerRedirectUri)) {
                    return false;
                }

                Object this$authorities = this.getAuthorities();
                Object other$authorities = other.getAuthorities();
                if (this$authorities == null) {
                    if (other$authorities != null) {
                        return false;
                    }
                } else if (!this$authorities.equals(other$authorities)) {
                    return false;
                }

                Object this$additionalInformation = this.getAdditionalInformation();
                Object other$additionalInformation = other.getAdditionalInformation();
                if (this$additionalInformation == null) {
                    if (other$additionalInformation != null) {
                        return false;
                    }
                } else if (!this$additionalInformation.equals(other$additionalInformation)) {
                    return false;
                }

                Object this$autoapprove = this.getAutoapprove();
                Object other$autoapprove = other.getAutoapprove();
                if (this$autoapprove == null) {
                    if (other$autoapprove != null) {
                        return false;
                    }
                } else if (!this$autoapprove.equals(other$autoapprove)) {
                    return false;
                }

                Object this$delFlag = this.getDelFlag();
                Object other$delFlag = other.getDelFlag();
                if (this$delFlag == null) {
                    if (other$delFlag != null) {
                        return false;
                    }
                } else if (!this$delFlag.equals(other$delFlag)) {
                    return false;
                }

                Object this$paySecret = this.getPaySecret();
                Object other$paySecret = other.getPaySecret();
                if (this$paySecret == null) {
                    if (other$paySecret != null) {
                        return false;
                    }
                } else if (!this$paySecret.equals(other$paySecret)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SysOauthClientDetails;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $accessTokenValidity = this.getAccessTokenValidity();
        result = result * 59 + ($accessTokenValidity == null ? 43 : $accessTokenValidity.hashCode());
        Object $refreshTokenValidity = this.getRefreshTokenValidity();
        result = result * 59 + ($refreshTokenValidity == null ? 43 : $refreshTokenValidity.hashCode());
        Object $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        Object $clientSecret = this.getClientSecret();
        result = result * 59 + ($clientSecret == null ? 43 : $clientSecret.hashCode());
        Object $resourceIds = this.getResourceIds();
        result = result * 59 + ($resourceIds == null ? 43 : $resourceIds.hashCode());
        Object $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        Object $authorizedGrantTypes = this.getAuthorizedGrantTypes();
        result = result * 59 + ($authorizedGrantTypes == null ? 43 : $authorizedGrantTypes.hashCode());
        Object $webServerRedirectUri = this.getWebServerRedirectUri();
        result = result * 59 + ($webServerRedirectUri == null ? 43 : $webServerRedirectUri.hashCode());
        Object $authorities = this.getAuthorities();
        result = result * 59 + ($authorities == null ? 43 : $authorities.hashCode());
        Object $additionalInformation = this.getAdditionalInformation();
        result = result * 59 + ($additionalInformation == null ? 43 : $additionalInformation.hashCode());
        Object $autoapprove = this.getAutoapprove();
        result = result * 59 + ($autoapprove == null ? 43 : $autoapprove.hashCode());
        Object $delFlag = this.getDelFlag();
        result = result * 59 + ($delFlag == null ? 43 : $delFlag.hashCode());
        Object $paySecret = this.getPaySecret();
        result = result * 59 + ($paySecret == null ? 43 : $paySecret.hashCode());
        return result;
    }
}
