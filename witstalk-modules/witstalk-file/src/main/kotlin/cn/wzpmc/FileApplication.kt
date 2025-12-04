package cn.wzpmc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["cn.wzpmc.properties"])
class FileApplication {
}
fun main(args: Array<String>) {
    runApplication<FileApplication>(*args)
}