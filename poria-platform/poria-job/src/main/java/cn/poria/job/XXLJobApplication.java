package cn.poria.job;

import cn.poria.common.feign.annotation.EnablePlatFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 分布式任务调度模块
 */
@Slf4j
@EnablePlatFeignClients
@SpringBootApplication
public class XXLJobApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(XXLJobApplication.class);
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
