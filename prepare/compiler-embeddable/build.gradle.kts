import org.gradle.kotlin.dsl.support.serviceOf

description = "Kotlin Compiler (embeddable)"

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":compiler:build-tools:kotlin-build-tools-api"))
    runtimeOnly(kotlinStdlib())
    runtimeOnly(project(":kotlin-script-runtime"))
    runtimeOnly(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }
    runtimeOnly(project(":kotlin-daemon-embeddable"))
    runtimeOnly(libs.kotlinx.coroutines.core) { isTransitive = false }
}

sourceSets {
    "main" {}
}

val runtimeJar = runtimeJar(embeddableCompiler()) {
    exclude("com/sun/jna/**")
    exclude("org/jetbrains/annotations/**")
    mergeServiceFiles()
}

val sourcesJar = sourcesJar {
    val compilerTask = project(":kotlin-compiler").tasks.named<Jar>("sourcesJar")
    dependsOn(compilerTask)
    val archiveOperations = serviceOf<ArchiveOperations>()
    from(compilerTask.map { it.archiveFile }.map { archiveOperations.zipTree(it) })
}

publish {
    setArtifacts(listOf(runtimeJar, sourcesJar))
}
