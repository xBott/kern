dependencies {
    implementation(project(":kern-meta:core"))

    compileOnly(libs.google.auto.service)
    annotationProcessor(libs.google.auto.service)

}