plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.rsocket.kotlin:rsocket-core:0.15.4")
    implementation("io.rsocket.kotlin:rsocket-transport-ktor-tcp:0.15.4")

    testImplementation(kotlin("test"))
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
