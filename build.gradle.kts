plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "it.adami.service.blog"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.adami.service.blog.AppKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}