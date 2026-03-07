import io.github.klahap.dotenv.DotEnvBuilder

plugins {
    java
    id("io.github.klahap.dotenv") version "1.1.3"
    id("maven-publish")
}

allprojects {
    project.group = "me.bottdev"
    project.version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.github.klahap.dotenv")
    apply(plugin = "maven-publish")

    val envVars = DotEnvBuilder.dotEnv {
        addFile("$rootDir/.env")
    }
    extra["envVars"] = envVars

    dependencies {

        implementation("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")

        testImplementation("org.projectlombok:lombok:1.18.38")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    publishing {
        repositories {
            maven {
                name = project.name
                url = uri("https://repo.nimbra.net/releases")
                credentials {
                    username = envVars["REPO_USERNAME"].toString()
                    password = envVars["REPO_PASSWORD"].toString()
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = "me.bottdev"
                artifactId = project.name
                version = "${project.version}"
                from(components["java"])
            }
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}
