package cn.poria.upms;

import cn.poria.common.feign.annotation.EnablePlatFeignClients;
import cn.poria.common.security.annotation.EnablePlatResourceServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.UnknownHostException;

@EnablePlatResourceServer
@EnablePlatFeignClients
@SpringBootApplication
@Slf4j
public class UpmsApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(UpmsApplication.class, args);
    }
}
