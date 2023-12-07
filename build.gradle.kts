plugins {
    id("java")
    id("org.springframework.boot") version ("3.2.0")
    id("checkstyle")
}
apply(plugin = "io.spring.dependency-management")

group = "com.bol"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation ("org.springframework.boot:spring-boot-starter-websocket")
    implementation ("org.springframework.boot:spring-boot-starter-reactor-netty")
    implementation ("org.springframework.boot:spring-boot-starter-validation")
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
}

checkstyle {
    configFile =  rootProject.file("checkstyle/checkstyle.xml")
}

tasks.test {
    useJUnitPlatform()
}