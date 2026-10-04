import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.jvm.Jvm

description = "Kotlin \"main\" script definition"

plugins {
    kotlin("jvm")
}

val jarBaseName = the<BasePluginExtension>().archivesName

val localPackagesToRelocate =
    listOf(
        "kotlinx.coroutines"
    )

dependencies {
    compileOnly(project(":compiler:cli-base"))
    compileOnly(project(":kotlin-scripting-jvm-host-unshaded"))
    compileOnly(project(":kotlin-scripting-dependencies-maven"))
    runtimeOnly(project(":kotlin-scripting-compiler-embeddable"))
    runtimeOnly(kotlinStdlib())
    runtimeOnly(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }
    embedded(project(":kotlin-scripting-common")) { isTransitive = false }
    embedded(project(":kotlin-scripting-jvm")) { isTransitive = false }
    embedded(project(":kotlin-scripting-jvm-host-unshaded")) { isTransitive = false }
    embedded(project(":kotlin-scripting-dependencies")) { isTransitive = false }
    embedded(project(":kotlin-scripting-dependencies-maven-all")) { isTransitive = false }
    embedded("org.slf4j:slf4j-api:1.7.36")
    embedded("org.slf4j:slf4j-simple:1.7.36")
    embedded(commonDependency("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm")) {
        isTransitive = false
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        }
    }

    testImplementation(project(":kotlin-scripting-dependencies"))
    testImplementation(libs.junit4)
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

publish()

noDefaultJar()

val embeddedConfiguration = configurations.named("embedded")
val relocatedJar by task<ShadowJar> {
    configurations.set(setOf(embeddedConfiguration.get()))
    from(mainSourceSet.output)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    archiveClassifier.set("shadow")

    // don't add this files to resources classpath to avoid IDE exceptions on kotlin project
    from("jar-resources")

    if (kotlinBuildProperties.relocation) {
        (packagesToRelocate + localPackagesToRelocate).forEach {
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
sourcesJar()
javadocJar()
