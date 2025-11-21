package cn.wzpmc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
        log.info("(♥◠‿◠)ﾉﾞ  文件服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
