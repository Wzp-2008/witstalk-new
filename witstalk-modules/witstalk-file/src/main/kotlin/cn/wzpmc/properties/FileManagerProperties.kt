package cn.wzpmc.properties

import cn.wzpmc.utils.log
import lombok.Data
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import java.io.File

@Slf4j
@ConfigurationProperties(prefix = "wzp.filemanager")
@Data
class FileManagerProperties {
    lateinit var savePath: File
    private var hmacKey = "RANDOM"
    private var dev = false

    @Bean
    fun savePath(): File {
        if (!savePath.isDirectory()) {
            if (!savePath.mkdirs()) {
                log.error("创建存储文件夹失败！")
                throw RuntimeException("创建存储文件夹失败！")
            }
        }
        return savePath
    }
}