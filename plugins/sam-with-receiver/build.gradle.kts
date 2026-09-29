description = "Kotlin SamWithReceiver Compiler Plugin"

plugins {
    kotlin("jvm")
}

dependencies {
    embedded(project(":kotlin-sam-with-receiver-compiler-plugin.common")) { isTransitive = false }
    embedded(project(":kotlin-sam-with-receiver-compiler-plugin.k1")) { isTransitive = false }
    embedded(project(":kotlin-sam-with-receiver-compiler-plugin.k2")) { isTransitive = false }
    embedded(project(":kotlin-sam-with-receiver-compiler-plugin.cli")) { isTransitive = false }
}

optInToExperimentalCompilerApi()

sourceSets {
    "main" { none() }
}

publish()

runtimeJar()
sourcesJar()
