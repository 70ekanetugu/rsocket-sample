
group = "jp.kanetugu70e.rsocket.server"
version = "1.0-SNAPSHOT"

dependencies {
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent
    implementation("org.springframework.boot:spring-boot-starter-parent:3.1.1")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-rsocket
    implementation("org.springframework.boot:spring-boot-starter-rsocket:3.1.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.AMAZON)
    }
}

kotlin {
    jvmToolchain(17)
}
