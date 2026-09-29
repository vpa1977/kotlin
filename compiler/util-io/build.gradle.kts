plugins {
    kotlin("jvm")
}

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    implementation(kotlin("stdlib", coreDepsVersion))
}

sourceSets {
    "main" { projectDefault() }
}

configureKotlinCompileTasksGradleCompatibility()

publish()

runtimeJar()
sourcesJar()
