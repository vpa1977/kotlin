description = "Kotlin Daemon Client"

plugins {
    kotlin("jvm")
}

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    api(kotlin("stdlib", coreDepsVersion))
    compileOnly(project(":daemon-common")) { exclude("org.jetbrains.kotlin", "kotlin-stdlib") }
    compileOnly(project(":js:js.config")) { exclude("org.jetbrains.kotlin", "kotlin-stdlib") }

    embedded(project(":daemon-common")) { isTransitive = false }
}

configureKotlinCompileTasksGradleCompatibility()

publish()

runtimeJar()
sourcesJar()
