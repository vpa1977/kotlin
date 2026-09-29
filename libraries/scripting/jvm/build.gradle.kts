import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

project.configureJvmToolchain(JdkMajorVersion.JDK_1_8)

dependencies {
    api(project(":kotlin-script-runtime"))
    api(kotlinStdlib())
    api(project(":kotlin-scripting-common"))
}

sourceSets {
    "main" { projectDefault() }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xallow-kotlin-package")
}

publish()

runtimeJar()
sourcesJar()
