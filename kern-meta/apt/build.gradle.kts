dependencies {
    implementation(project(":kern-commons"))
    implementation(project(":kern-meta:core"))

    implementation(libs.guava)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)

    testImplementation(libs.assertj.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

}