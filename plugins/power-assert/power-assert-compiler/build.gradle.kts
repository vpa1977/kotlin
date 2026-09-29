description = "Kotlin Power-Assert Compiler Plugin"

plugins {
    kotlin("jvm")
}

dependencies {
    embedded(project(":kotlin-power-assert-compiler-plugin.common")) { isTransitive = false }
    embedded(project(":kotlin-power-assert-compiler-plugin.backend")) { isTransitive = false }
    embedded(project(":kotlin-power-assert-compiler-plugin.frontend")) { isTransitive = false }
    embedded(project(":kotlin-power-assert-compiler-plugin.cli")) { isTransitive = false }
}

optInToExperimentalCompilerApi()

sourceSets {
    "main" { none() }
}

publish()

runtimeJar()
sourcesJar()
