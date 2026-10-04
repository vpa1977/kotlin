@file:Suppress("HasPlatformType")

description = "Kotlin Compiler"

plugins {
    // HACK: java plugin makes idea import dependencies on this project as source (with empty sources however),
    // this prevents reindexing of kotlin-compiler.jar after build on every change in compiler modules
    `java-library`
    // required to disambiguate attributes of non-jvm Kotlin libraries
    kotlin("jvm")
}


val fatJarContents by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    attributes {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
    }
}
val fatJarContentsStripMetadata by configurations.creating
val fatJarContentsStripServices by configurations.creating
val fatJarContentsStripVersions by configurations.creating

val compilerVersion by configurations.creating

val builtinsMetadata by configurations.creating

val api by configurations

val compilerModules: Array<String> by rootProject.extra

configurations.all {
    resolutionStrategy {
        preferProjectModules()
    }
}

dependencies {
    api(kotlinStdlib("jdk8"))
    api(project(":kotlin-script-runtime"))
    api(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }
    api(libs.kotlinx.coroutines.core)
    api(project(":compiler:build-tools:kotlin-build-tools-api"))

    compilerVersion(project(":compiler:compiler.version"))
    compilerModules
        .filter { it != ":compiler:compiler.version" } // Version will be added directly to the final jar
        .forEach {
            fatJarContents(project(it)) { isTransitive = false }
        }

    fatJarContents(commonDependency("javax.inject"))
    fatJarContents(commonDependency("org.jline", "jline"))
    fatJarContents(commonDependency("org.fusesource.jansi", "jansi"))
    fatJarContents(protobufFull())
    fatJarContents(commonDependency("com.google.code.findbugs", "jsr305"))
    fatJarContents(libs.vavr)
    fatJarContents(commonDependency("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm")) { isTransitive = false }

    fatJarContents(intellijCore())
    fatJarContents(commonDependency("org.jetbrains.intellij.deps.jna:jna")) { isTransitive = false }
    fatJarContents(commonDependency("org.jetbrains.intellij.deps.jna:jna-platform")) { isTransitive = false }
    fatJarContents(libs.intellij.fastutil)
    fatJarContents(commonDependency("org.lz4:lz4-java")) { isTransitive = false }
    fatJarContents(libs.intellij.asm) { isTransitive = false }
    fatJarContents(libs.guava) { isTransitive = false }
    fatJarContents(libs.guava.failureaccess) { isTransitive = false }

    fatJarContentsStripServices(commonDependency("com.fasterxml:aalto-xml")) { isTransitive = false }
    fatJarContents(commonDependency("org.codehaus.woodstox:stax2-api")) { isTransitive = false }

    fatJarContentsStripMetadata(commonDependency("oro:oro")) { isTransitive = false }
    fatJarContentsStripMetadata(intellijJDom()) { isTransitive = false }
    fatJarContentsStripMetadata(commonDependency("org.jetbrains.intellij.deps:log4j")) { isTransitive = false }
    fatJarContentsStripVersions(commonDependency("one.util:streamex")) { isTransitive = false }

    // Used by JS parser
    fatJarContents(libs.antlr.runtime) { isTransitive = false }

    builtinsMetadata(kotlinStdlib())
}

val packCompiler by task<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    archiveClassifier.set("unshrunk")

    dependsOn(fatJarContents)
    from {
        fatJarContents.map(::zipTree)
    }

    dependsOn(fatJarContentsStripServices)
    from {
        fatJarContentsStripServices.files.map {
            zipTree(it).matching { exclude("META-INF/services/**") }
        }
    }

    dependsOn(fatJarContentsStripMetadata)
    from {
        fatJarContentsStripMetadata.files.map {
            zipTree(it).matching { exclude("META-INF/jb/**", "META-INF/LICENSE") }
        }
    }

    dependsOn(fatJarContentsStripVersions)
    from {
        fatJarContentsStripVersions.files.map {
            zipTree(it).matching {
                includeEmptyDirs = false
                exclude("META-INF/versions/**")
            }
        }
    }

    dependsOn(builtinsMetadata)
    from {
        builtinsMetadata.files.map {
            zipTree(it).matching { include("**/*.kotlin_builtins") }
        }
    }
}

val jar = runtimeJar {
    dependsOn(packCompiler)
    dependsOn(compilerVersion)

    from {
        packCompiler.map { zipTree(it.singleOutputFile(layout)) }
    }

    from {
        compilerVersion.map(::zipTree)
    }

    manifest.attributes["Class-Path"] = compilerManifestClassPath
    manifest.attributes["Main-Class"] = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"
}

sourcesJar {
    from {
        compilerModules.map {
            project(it).mainSourceSet.allSource
        }
    }

    dependsOn(":compiler:fir:checkers:generateCheckersComponents", ":compiler:ir.tree:generateTree")
}
