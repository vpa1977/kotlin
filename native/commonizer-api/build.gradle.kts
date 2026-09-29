plugins {
    kotlin("jvm")
    id("gradle-plugin-published-compiler-dependency-configuration")
}

kotlin {
    explicitApi()
}

description = "Kotlin KLIB Library Commonizer API"
publish()

dependencies {
    implementation(kotlinStdlib())
    implementation(project(":native:kotlin-native-utils"))
}

sourceSets {
    "main" { projectDefault() }
}

runtimeJar()
sourcesJar()
