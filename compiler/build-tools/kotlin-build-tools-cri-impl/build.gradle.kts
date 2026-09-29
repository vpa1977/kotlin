plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":compiler:build-tools:kotlin-build-tools-api"))
    implementation(kotlinStdlib())
    compileOnly(project(":kotlin-build-common"))
    compileOnly(project(":core:compiler.common"))

    compileOnly(libs.kotlinx.serialization.protobuf)
    embedded(libs.kotlinx.serialization.protobuf) { isTransitive = false }
    embedded(libs.kotlinx.serialization.core) { isTransitive = false }
}

publish()

runtimeJarWithRelocation {
    from(mainSourceSet.output)
    relocate("kotlinx.serialization", "org.jetbrains.kotlin.buildtools.internal.serialization")
}

sourcesJar()

kotlin {
    explicitApi()
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi")
    }
}
