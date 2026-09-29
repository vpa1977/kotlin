plugins {
    kotlin("jvm")
}

description = "Kotlin/Native utils"

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    compileOnly(kotlin("stdlib", coreDepsVersion))
    api(project(":kotlin-util-io"))
    api(project(":kotlin-util-klib"))
    api(platform(project(":kotlin-gradle-plugins-bom")))
}

sourceSets {
    "main" { projectDefault() }
}

configureKotlinCompileTasksGradleCompatibility()

publish()

runtimeJar {
    // JPMS can't handle the jar file name. Fix that by specifying a valid module name in the manifest:
    manifest.attributes["Automatic-Module-Name"] = "kotlin.native_utils"
    // See https://youtrack.jetbrains.com/issue/KT-72063.
}

sourcesJar()
