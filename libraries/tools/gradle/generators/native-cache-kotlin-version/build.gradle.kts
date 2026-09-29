plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(project(":generators"))
    implementation(commonDependency("org.jetbrains.kotlin:kotlin-reflect"))
}
