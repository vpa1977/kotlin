plugins {
    kotlin("jvm")
}

description = "Common klib reader and writer"

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    api("org.jetbrains.kotlin:kotlin-stdlib:$coreDepsVersion")
    api(project(":kotlin-util-io"))

    compileOnly(project(":core:metadata")) { exclude("org.jetbrains.kotlin", "kotlin-stdlib") }

    embedded(project(":core:metadata")) { isTransitive = false }
}

sourceSets {
    "main" { projectDefault() }
}

configureKotlinCompileTasksGradleCompatibility()

publish()

runtimeJar()
sourcesJar()
