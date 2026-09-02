import io.github.klahap.dotenv.DotEnvBuilder

plugins {
    java
    id("io.github.klahap.dotenv") version "1.1.3"
    id("maven-publish")
}

val rootEnvVars = DotEnvBuilder.dotEnv {
    addFile("$rootDir/.env")
}

allprojects {
    project.group = "me.bottdev"
    project.version = "0.0.47-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.github.klahap.dotenv")
    apply(plugin = "maven-publish")

    extra["envVars"] = rootEnvVars

    dependencies {
        testImplementation(platform(rootProject.libs.junit.bom))
        testImplementation(rootProject.libs.junit.jupiter)

        testImplementation(rootProject.libs.mockito.core)
        testImplementation(rootProject.libs.mockito.junit.jupiter)

        testImplementation(rootProject.libs.assertj.core)

        compileOnly(rootProject.libs.lombok)
        testCompileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
    }

    publishing {
        repositories {
            maven {

                name = project.name
                url = if (version.toString().endsWith("-SNAPSHOT"))
                    uri("https://reposlite.nimbra.net/snapshots")
                else
                    uri("https://reposlite.nimbra.net/releases")

                credentials {
                    username = rootEnvVars["REPO_USERNAME"].toString()
                    password = rootEnvVars["REPO_PASSWORD"].toString()
                }

                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = "me.bottdev"
                artifactId = project.path.substring(1).replace(":", "-")
                version = "${project.version}"
                from(components["java"])
            }
        }
    }

    tasks.jar {
        archiveBaseName = project.path.substring(1).replace(":", "-")
    }

    tasks.test {
        useJUnitPlatform()
    }

}