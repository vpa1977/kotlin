description = "ABI generation for Kotlin/JVM"

plugins {
    kotlin("jvm")
}

sourceSets {
    "main" { projectDefault() }
}

val embedded by configurations
embedded.isTransitive = false
configurations.getByName("compileOnly").extendsFrom(embedded)

dependencies {
    compileOnly(project(":compiler:util"))
    compileOnly(project(":compiler:cli"))
    compileOnly(project(":compiler:backend"))
    compileOnly(project(":compiler:backend.jvm"))
    compileOnly(project(":compiler:frontend"))
    compileOnly(project(":compiler:frontend.java"))
    compileOnly(project(":compiler:plugin-api"))

    // Include kotlin.metadata for metadata stripping.
    // Note that kotlin-metadata-jvm already includes kotlin-metadata, core:metadata, core:metadata.jvm,
    // and protobuf-lite, so we only need to include kotlin-metadata-jvm in the shadow jar.
    compileOnly(project(":kotlin-metadata"))
    embedded(project(":kotlin-metadata-jvm"))

    compileOnly(intellijCore())
    compileOnly(libs.intellij.asm)
}

optInToExperimentalCompilerApi()

publish()

runtimeJarWithRelocation {
    from(mainSourceSet.output)
    relocate("kotlinx.metadata", "org.jetbrains.kotlin.jvm.abi.kotlinx.metadata")
    mergeServiceFiles() // This is needed to relocate the services files for kotlinx.metadata
}

sourcesJar()
