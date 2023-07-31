plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.2")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.3.2")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.4.8")


    implementation("io.rsocket.kotlin:rsocket-ktor-client:0.15.4")
    implementation("io.rsocket.kotlin:rsocket-ktor-server:0.15.4")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-java:2.3.2")

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

kotlin {
    jvmToolchain(19)
}

tasks.test {
    useJUnitPlatform()
}
