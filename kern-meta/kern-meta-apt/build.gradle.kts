dependencies {
    implementation(project(":kern-meta:kern-meta-core"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)

    testImplementation(libs.assertj.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.google.auto.service)
    annotationProcessor(libs.google.auto.service)

}