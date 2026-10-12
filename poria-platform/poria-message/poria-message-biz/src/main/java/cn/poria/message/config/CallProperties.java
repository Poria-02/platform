package cn.poria.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "call")
public class CallProperties {

    private String url;

    private String appId;

    private String secretKey;

}
