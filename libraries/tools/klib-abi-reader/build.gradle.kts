import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

description = "KLIB ABI reader"

plugins {
    kotlin("jvm")
}

val jarBaseName = the<BasePluginExtension>().archivesName

val embedded by configurations

val relocatedJarContents by configurations.creating {
    extendsFrom(embedded)
}

dependencies {
    runtimeOnly(kotlinStdlib())
    embedded(project(":kotlin-util-klib-abi")) {
        isTransitive = true
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }
    embedded(libs.guava)
    embedded(libs.intellij.fastutil)
}

noDefaultJar()

val relocatedJar by task<ShadowJar> {
    configurations = listOf(embedded)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    archiveClassifier.set("before-proguard")

    if (kotlinBuildProperties.relocation) {
        packagesToRelocate.forEach {
            relocate(it, "$kotlinEmbeddableRootPackage.$it")
        }
    }
}

val resultJar by task<Jar> {
    dependsOn(relocatedJar)
    setupPublicJar(jarBaseName)
    from {
        zipTree(relocatedJar.map { it.singleOutputFile(layout) })
    }
}

setPublishableArtifact(resultJar)
