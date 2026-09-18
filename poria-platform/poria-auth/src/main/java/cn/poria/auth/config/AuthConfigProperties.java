package cn.poria.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("auth")
public class AuthConfigProperties {

    public String encodeKey;

    public boolean enable;

    public String cLHisTokenUrl;

    public String tSHisTokenUrl;

}
