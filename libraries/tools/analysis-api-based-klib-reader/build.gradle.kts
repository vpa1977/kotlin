import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

sourceSets {
    "main" { projectDefault() }
}

kotlin {
    compilerOptions {
        explicitApi()
    }
}

dependencies {
    api(project(":analysis:analysis-api"))

    implementation(project(":core:compiler.common"))
    implementation(project(":kotlin-tooling-core"))

    compileOnly(project(":analysis:analysis-api-standalone"))
    compileOnly(project(":core:metadata"))
    compileOnly(project(":kotlin-metadata"))
    compileOnly(project(":kotlin-util-klib-metadata"))
    compileOnly(protobufLite())
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
}
