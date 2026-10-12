package cn.poria.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yxxh")
public class YxxhProperties {

    private String baseUrl;

    private String appId;

    private String token;

}
