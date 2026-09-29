plugins {
    java
    kotlin("jvm")
}

configureKotlinCompileTasksGradleCompatibility()

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    implementation(kotlin("stdlib", coreDepsVersion))
    implementation(commonDependency("com.google.code.gson:gson"))
}
