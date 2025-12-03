plugins {
    val ktVersion = "1.9.25"
    kotlin("jvm") version ktVersion
    kotlin("plugin.spring") version ktVersion
    // kotlin annotation processor
    kotlin("kapt") version ktVersion
}

group = "cn.wzpmc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    kapt("com.mybatis-flex:mybatis-flex-processor:1.11.4")
    implementation(project(":witstalk-modules:witstalk-system"))
    // https://mvnrepository.com/artifact/commons-codec/commons-codec
    implementation("commons-codec:commons-codec:1.17.1")
    // https://mvnrepository.com/artifact/org.apache.tika/tika-core
    implementation("org.apache.tika:tika-core:3.2.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
