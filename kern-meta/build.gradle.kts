plugins {
    kotlin("jvm") version "2.3.10"
}

group = "me.bottdev"
version = "0.0.17-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}