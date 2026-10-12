package cn.poria.message.service.video.tx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tx.trtc")
public class TxTRTCProperties {

    /**
     * 腾讯云 SDKAppId，需要替换为您自己账号下的 SDKAppId。
     */
    private Long sdkAppId;

    private String secretId;

    /**
     * 计算签名用的加密密钥，获取步骤如下：
     */
    private String secretKey;

    private String adminAccountName;

    /**
     * 签名过期时间，建议不要设置的过短
     * <p>
     * 时间单位：秒
     * 默认时间：7 x 24 x 60 x 60 = 604800 = 7 天
     */
    private int singExpireTime;

    private String callbackUrl;
}
