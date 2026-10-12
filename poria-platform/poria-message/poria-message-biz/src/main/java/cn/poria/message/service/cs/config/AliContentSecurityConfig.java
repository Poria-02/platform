package cn.poria.message.service.cs.config;

import cn.poria.message.config.CsProperties;
import com.aliyun.green20220302.Client;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * author qiaodi
 * date 2025/9/9 09:54
 * version 6.7.3
 * description
 */
@Configuration
public class AliContentSecurityConfig {

    @Resource
    private CsProperties csProperties;

    @Bean("aliContentSecurityClient")
    @Lazy
    public Client aliContentSecurityClient() throws Exception {
        Config config = new Config();
        /**
         * 阿里云账号AccessKey拥有所有API的访问权限，建议您使用RAM用户进行API访问或日常运维。
         * 常见获取环境变量方式：
         * 方式一：
         *     获取RAM用户AccessKey ID：System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
         *     获取RAM用户AccessKey Secret：System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
         * 方式二：
         *     获取RAM用户AccessKey ID：System.getProperty("ALIBABA_CLOUD_ACCESS_KEY_ID");
         *     获取RAM用户AccessKey Secret：System.getProperty("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
         */
        config.setAccessKeyId(csProperties.getAccessKeyId());
        config.setAccessKeySecret(csProperties.getAccessKeySecret());
        //接入区域和地址请根据实际情况修改
        config.setRegionId("cn-beijing");
        config.setEndpoint("green-cip.cn-beijing.aliyuncs.com");
        //设置http代理。
        //config.setHttpProxy("http://xx.xx.xx.xx:xxxx");
        //设置https代理。
        //config.setHttpsProxy("https://xx.xx.xx.xx:xxxx");
        return new Client(config);
    }
}
