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

val proguardLibraryJars by configurations.creating {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
    }
}

dependencies {
    api(kotlinStdlib())
    embedded(project(":kotlin-metadata"))
    embedded(project(":core:metadata"))
    embedded(project(":core:metadata.jvm"))
    embedded(protobufLite())

    proguardLibraryJars(kotlinStdlib())
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

val proguard by task<CacheableProguardTask> {
    dependsOn(relocatedJar)

    injars(mapOf("filter" to "!META-INF/versions/**"), relocatedJar.get().outputs.files)
    outjars(fileFrom(base.libsDirectory.asFile.get(), "${base.archivesName.get()}-$version-proguard.jar"))

    javaLauncher.set(project.getToolchainLauncherFor(JdkMajorVersion.JDK_1_8))

    libraryjars(mapOf("filter" to "!META-INF/versions/**"), proguardLibraryJars)
    libraryjars(
        project.files(
            javaLauncher.map {
                firstFromJavaHomeThatExists(
                    "jre/lib/rt.jar",
                    "../Classes/classes.jar",
                    jdkHome = it.metadata.installationPath.asFile
                )!!
            }
        )
    )

    configuration("metadata.pro")
}

val resultJar by task<Jar> {
    val pack = if (kotlinBuildProperties.proguard) proguard else relocatedJar
    dependsOn(pack)
    setupPublicJar(base.archivesName.get())
    from {
        zipTree(pack.get().singleOutputFile(layout))
    }

    manifest {
        attributes("Automatic-Module-Name" to "kotlin.metadata.jvm")
    }
}

setPublishableArtifact(resultJar)

sourcesJar()
