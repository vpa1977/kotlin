import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

/**
 * Resolves [sourceConfiguration], searches for [dependenciesToInherit] in this configuration and puts the found dependency versions in
 * [targetConfiguration]. Also creates and returns a task that fails if dependencies were not found in the [sourceConfiguration]
 */
fun Project.inheritAndValidateExternalDependencies(
    sourceConfiguration: Configuration,
    targetConfiguration: Configuration,
    dependenciesToInherit: Map<String, String>,
): TaskProvider<*> {
    val inheritedDependencies = sourceConfiguration.incoming.resolutionResult.allComponents.filter {
        val moduleVersion = it.moduleVersion ?: return@filter false
        dependenciesToInherit[moduleVersion.group] == moduleVersion.name
    }.map { it.moduleVersion!! }

    inheritedDependencies.forEach {
        targetConfiguration.dependencies.add(
            dependencies.create("${it.group}:${it.name}:${it.version}")
        )
    }

    return tasks.register("validateInheritedDependencies") {
        doLast {
            if (inheritedDependencies.size != dependenciesToInherit.size) {
                error("Actual inherited dependencies: $inheritedDependencies")
            }
        }
    }
}

/**
 * This task makes sure there are no duplicates in the runtime classpath; i.e. runtimeOnly dependencies are correctly specified and
 * there is no collision with embedded classes
 */
fun Project.validateEmbeddedJarRuntimeClasspathHasNoDuplicates(
    embeddedJar: TaskProvider<out Jar>,
    runtimeClasspath: ConfigurableFileCollection,
) = tasks.register("validateEmbeddedJarRuntimeClasspathHasNoDuplicates") {
    dependsOn(embeddedJar)
    dependsOn(runtimeClasspath)

    fun String.isImplementationClassFile() = endsWith(".class") && !endsWith("module-info.class")
    fun File.forEachClassFileInAJar(action: (ZipEntry) -> (Unit)) = ZipFile(this).use { zip ->
        zip.entries().asSequence().filter {
            it.name.isImplementationClassFile()
        }.forEach { action(it) }
    }
    val embeddedJarFiles = files(embeddedJar)

    doLast {
        val duplicates = mutableMapOf<String, MutableList<File>>()
        (embeddedJarFiles + runtimeClasspath).forEach { jar ->
            jar.forEachClassFileInAJar { entry ->
                duplicates.getOrPut(entry.name, ::mutableListOf).add(jar)
            }
        }
        val duplicateClassfiles = duplicates.filter { it.value.size > 1 }
        if (duplicateClassfiles.isNotEmpty()) {
            error(
                """
                    |Duplicates in runtime classpath:
                    |${duplicateClassfiles.map { "${it.key}: ${it.value}" }.joinToString("\n")}
                """.trimMargin()
            )
        }
    }
}