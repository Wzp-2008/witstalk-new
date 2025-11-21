plugins {
    id("java")
}

group = "top.xinsin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation(project(":witstalk-common"))
}
