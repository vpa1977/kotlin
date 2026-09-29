description = "Kotlin Assignment Compiler Plugin"

plugins {
    kotlin("jvm")
}

dependencies {
    embedded(project(":kotlin-assignment-compiler-plugin.common")) { isTransitive = false }
    embedded(project(":kotlin-assignment-compiler-plugin.k1")) { isTransitive = false }
    embedded(project(":kotlin-assignment-compiler-plugin.k2")) { isTransitive = false }
    embedded(project(":kotlin-assignment-compiler-plugin.cli")) { isTransitive = false }
}

optInToExperimentalCompilerApi()

sourceSets {
    "main" { none() }
}

publish()

runtimeJar()
sourcesJar()
