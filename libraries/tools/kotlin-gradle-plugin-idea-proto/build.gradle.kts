@file:Suppress("HasPlatformType")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi")
    }
}

val embedded by configurations.getting {
    isTransitive = false
    configurations.getByName("compileOnly").extendsFrom(this)
}

dependencies {
    api(project(":kotlin-tooling-core"))
    api(project(":kotlin-gradle-plugin-idea"))
    embedded(libs.protobuf.java)
    embedded(libs.protobuf.kotlin)
}

configureKotlinCompileTasksGradleCompatibility()

sourceSets.main.configure {
    java.srcDir("src/generated/java")
    java.srcDir("src/generated/kotlin")
}

publish()

sourcesJar()
runtimeJar(tasks.register<ShadowJar>("embeddable")) {
    from(mainSourceSet.output)
    exclude("**/*.proto")
    relocate("com.google.protobuf", "org.jetbrains.kotlin.gradle.idea.proto.com.google.protobuf")
}
