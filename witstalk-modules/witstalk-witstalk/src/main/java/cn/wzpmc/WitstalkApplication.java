package cn.wzpmc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class WitstalkApplication {
    public static void main(String[] args) {
        SpringApplication.run(WitstalkApplication.class,args);
        log.info("(♥◠‿◠)ﾉﾞ  Witstalk服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
