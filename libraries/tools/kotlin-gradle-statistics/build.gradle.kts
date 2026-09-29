description = "kotlin-gradle-statistics"

plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

configureKotlinCompileTasksGradleCompatibility()
extensions.extraProperties["kotlin.stdlib.default.dependency"] = "false"

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    compileOnly(kotlin("stdlib", coreDepsVersion))
}
