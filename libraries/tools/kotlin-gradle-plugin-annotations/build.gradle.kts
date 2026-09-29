plugins {
    kotlin("jvm")
}

configureKotlinCompileTasksGradleCompatibility()

publish()
runtimeJar()
sourcesJar()

dependencies {
    api(platform(project(":kotlin-gradle-plugins-bom")))

    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:$coreDepsVersion")
}
