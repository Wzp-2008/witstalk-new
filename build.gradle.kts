extra.set("mybatis-flex-version", "1.11.4")
extra.set("lombok-version", "1.18.42")
plugins {
    id("java")
    // 声明 Spring Boot 插件（不直接应用）
    id("org.springframework.boot") version "3.5.8" apply false
    // 声明依赖管理插件（关键：必须显式声明）
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "top.xinsin"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
    }
}

subprojects {
    // 1. 强制应用基础插件
    apply(plugin = "java")
    // 2. 关键：显式应用依赖管理插件（必须添加）
    apply(plugin = "io.spring.dependency-management")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 3. 使用插件提供的扩展（此时已能识别）
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.8")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
            mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.0.0.0")
        }
    }

    dependencies {
        implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
//        redis
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
//        负载均衡
        implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
//        远程调用
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
//        nacos配置中心和注册中心
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
//        fastjson处理
        implementation("com.alibaba.fastjson2:fastjson2:2.0.60")
        // Lombok 核心依赖
        implementation("org.projectlombok:lombok:${rootProject.extra.get("lombok-version")}")
        // 注解处理器（必须添加，否则 Lombok 注解不生效）
        annotationProcessor("org.projectlombok:lombok:${rootProject.extra.get("lombok-version")}")
        // 测试环境支持（如需在测试类中使用 Lombok）
        testImplementation("org.projectlombok:lombok:${rootProject.extra.get("lombok-version")}")
        testAnnotationProcessor("org.projectlombok:lombok:${rootProject.extra.get("lombok-version")}")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}

// 可执行模块列表
val executableModules = listOf(
    ":witstalk-gateway" to "top.xinsin.GatewayApplication",
    ":witstalk-auth" to "top.xinsin.AuthApplication",
    ":witstalk-modules:witstalk-system" to "top.xinsin.SystemApplication",
    ":witstalk-modules:witstalk-game" to "top.xinsin.GameApplication",
    ":witstalk-modules:witstalk-witstalk" to "cn.wzpmc.WitstalkApplication",
    ":witstalk-modules:witstalk-file" to "cn.wzpmc.FileApplication"
)

// 为可执行模块应用Spring Boot插件并配置
subprojects {
    if (executableModules.any { it.first == project.path }) {
        apply(plugin = "org.springframework.boot")
        
        // 配置bootJar任务
        tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
            val moduleName = project.path.substringAfterLast(":")
            archiveBaseName.set(moduleName)
            archiveVersion.set("1.0.0")
            
            // 设置主类
            val mainClass = executableModules.find { it.first == project.path }?.second
            if (mainClass != null) {
                this.mainClass.set(mainClass)
            }
        }
    }
}
