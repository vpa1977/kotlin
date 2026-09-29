description = "Kotlin Daemon"

plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":compiler:cli"))
    compileOnly(project(":compiler:incremental-compilation-impl"))
    compileOnly(intellijCore())
    compileOnly(libs.intellij.fastutil)

    runtimeOnly(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }

    embedded(project(":daemon-common")) { isTransitive = false }
}

optInToExperimentalCompilerApi()

sourceSets {
    "main" { projectDefault() }
}

runtimeJar()

optInToK1Deprecation()
