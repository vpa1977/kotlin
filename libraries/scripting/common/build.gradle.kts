import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

project.updateJvmTarget("1.8")

dependencies {
    api(kotlinStdlib())
    compileOnly(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }
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
