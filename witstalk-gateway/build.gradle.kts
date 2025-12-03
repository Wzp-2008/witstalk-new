plugins {
    id("java")
}

group = "top.xinsin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation(project("::witstalk-common"))

    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.3.2")
    //    jwt
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
//        security
//    implementation("org.springframework.boot:spring-boot-starter-security")
}
