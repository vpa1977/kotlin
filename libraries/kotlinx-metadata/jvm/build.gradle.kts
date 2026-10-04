import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

description = "Kotlin JVM metadata manipulation library"
group = "org.jetbrains.kotlin"

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
    api(kotlinStdlib())
    embedded(project(":kotlin-metadata"))
    embedded(project(":core:metadata"))
    embedded(project(":core:metadata.jvm"))
    embedded(protobufLite())
}

kotlin {
    explicitApi()
    compilerOptions {
        freeCompilerArgs.add("-Xallow-kotlin-package")
    }
}

publish()
val unshaded by task<Jar> {
    archiveClassifier.set("unshaded")
    from(mainSourceSet.output)
}
project.addArtifact("unshaded", unshaded, unshaded)

val relocatedJar by task<ShadowJar> {
    configurations = listOf(embedded)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    archiveClassifier.set("shadow")

    from(mainSourceSet.output)
    exclude("**/*.proto")
    relocate("org.jetbrains.kotlin", "kotlin.metadata.internal")
}

val resultJar by task<Jar> {
    dependsOn(relocatedJar)
    setupPublicJar(base.archivesName.get())
    from {
        zipTree(relocatedJar.get().singleOutputFile(layout))
    }

    manifest {
        attributes("Automatic-Module-Name" to "kotlin.metadata.jvm")
    }
}

setPublishableArtifact(resultJar)

sourcesJar()
