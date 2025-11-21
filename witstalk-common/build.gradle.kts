plugins {
    id("java")
}

group = "top.xinsin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.mybatis-flex:mybatis-flex-spring-boot3-starter:1.11.4")
    //    jwt
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.4")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime:4.0.4")
}
