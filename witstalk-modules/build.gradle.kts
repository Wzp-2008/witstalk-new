plugins {
    id("java")
}

group = "top.xinsin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
subprojects {
    dependencies {
        implementation(project("::witstalk-common"))
        implementation(project("::witstalk-api"))
        implementation(project("::witstalk-common-autoconfigure"))
        implementation(project("::witstalk-entities"))

        implementation("org.springframework.boot:spring-boot-starter-web")
//    mybatis flex + druid连接池 + 驱动
        implementation("com.mybatis-flex:mybatis-flex-spring-boot3-starter:${rootProject.extra.get("mybatis-flex-version")}")
        annotationProcessor("com.mybatis-flex:mybatis-flex-processor:${rootProject.extra.get("mybatis-flex-version")}")
        implementation("com.alibaba:druid-spring-boot-starter:1.2.27")
        implementation("com.mysql:mysql-connector-j:8.4.0")

//        工具库
        implementation("cn.hutool:hutool-all:5.8.41")
    }
}
