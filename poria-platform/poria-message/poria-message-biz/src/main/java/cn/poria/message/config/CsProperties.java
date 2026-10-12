package cn.poria.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * author qiaodi
 * date 2025/9/8 17:37
 * version 6.7.0
 * description
 */

@Data
@Component
@ConfigurationProperties(prefix = "cs")
public class CsProperties {

    private String accessKeyId;

    private String accessKeySecret;

}
