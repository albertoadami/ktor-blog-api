val ktor_version: String by project
val kotlin_version: String by project
val exposed_version: String by project
val logback_version: String by project
val testContainer_version: String by project

plugins {
    application
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "3.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.3"

}

group = "it.adami.services.blog"
version = "0.0.1"


application {
    applicationName = "blog-api"
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.flywaydb:flyway-core:7.10.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.testcontainers:testcontainers:$testContainer_version")
    testImplementation("org.testcontainers:postgresql:$testContainer_version")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.0")  // or the latest compatible version
    testImplementation("io.kotest:kotest-assertions-core:5.6.0")  // make sure all Kotest dependencies are up-to-date
    implementation("org.mindrot:jbcrypt:0.4")
}

// Specify Java 21 toolchain
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}