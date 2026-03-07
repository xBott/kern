# Kern (W.I.P.)
Kern is a set of Java libraries that provide diverse functionality like graphs and advanced YAML / JSON configuration parser.

Modules:
- kern-struct (provides different data structures and algorithms)
- kern-noema (an advanced parser of YAML and JSON)

# Integration

Repository:
```kotlin
repositories {
    maven("https://repo.nimbra.net/releases")
}
```
Dependencies:
```kotlin
dependencies {
    implementation("me.bottdev:kern-struct:<version>")
    implementation("me.bottdev:kern-noema:<version>")
}
```