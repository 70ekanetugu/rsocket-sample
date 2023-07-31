
rootProject.name = "spring-boot-rsocket-sample"

plugins {
    // Gradle8.0 からツールチェーンリポジトリを明示する必要がある。
    // (これのおかげで各ベンダーJDKが利用できる)
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.5.0")
}

include("server", "client")
