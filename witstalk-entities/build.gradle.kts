plugins {
    id("java")
    val ktVersion = "1.9.25"
    kotlin("jvm") version ktVersion
    kotlin("plugin.spring") version ktVersion
    // kotlin annotation processor
    kotlin("kapt") version ktVersion
}

group = "cn.wzpmc"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":witstalk-common"))
    implementation("com.mybatis-flex:mybatis-flex-spring-boot3-starter:${rootProject.extra.get("mybatis-flex-version")}")
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor:${rootProject.extra.get("mybatis-flex-version")}")
    kapt("com.mybatis-flex:mybatis-flex-processor:${rootProject.extra.get("mybatis-flex-version")}")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.test {
    useJUnitPlatform()
}