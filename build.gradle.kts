plugins {
    java
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

    dependencies {

        implementation("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")

        testImplementation("org.projectlombok:lombok:1.18.38")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
