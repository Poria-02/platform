package cn.poria.auth;

import cn.poria.common.feign.annotation.EnablePlatFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 认证授权中心
 */
@EnableAsync
@Slf4j
@EnableCaching
@EnablePlatFeignClients
@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(AuthApplication.class);
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
