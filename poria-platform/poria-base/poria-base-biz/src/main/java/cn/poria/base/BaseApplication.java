package cn.poria.base;

import cn.poria.common.feign.annotation.EnablePlatFeignClients;
import cn.poria.common.security.annotation.EnablePlatResourceServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author pigx archetype
 * <p>
 * 项目启动类
 */
@Slf4j
@SpringBootApplication
@EnablePlatFeignClients
@EnablePlatResourceServer
public class BaseApplication {
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(BaseApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t"
                        + "Local: \t\thttp://localhost:{}\n\t"
                        + "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port"));
    }
}

