package cn.poria.upms;

import cn.poria.common.feign.annotation.EnablePlatFeignClients;
import cn.poria.common.security.annotation.EnablePlatResourceServer;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnablePlatResourceServer
@EnablePlatFeignClients
@SpringBootApplication
public class UpmsApplication {
   private static final Logger log = LoggerFactory.getLogger(UpmsApplication.class);

   public static void main(String[] args) throws UnknownHostException {
      SpringApplication.run(UpmsApplication.class, args);
   }
}
