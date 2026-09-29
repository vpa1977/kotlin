import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(project(":core:util.runtime"))
    implementation(project(":kotlin-util-io"))

    implementation(kotlinStdlib())
    implementation(kotlinxCollectionsImmutable())
}

sourceSets {
    "main" { projectDefault() }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.optIn.add("kotlin.ExperimentalUnsignedTypes")
    compilerOptions.freeCompilerArgs.add("-Xskip-prerelease-check")
}
