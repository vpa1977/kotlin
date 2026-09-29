plugins {
    kotlin("jvm")
}

val runtimeOnly by configurations
val compileOnly by configurations
runtimeOnly.extendsFrom(compileOnly)

dependencies {
    implementation(project(":generators:tree-generator-common"))
    implementation(project(":compiler:util"))

    compileOnly(intellijCore())

    runtimeOnly(intellijJDom())
}

sourceSets {
    "main" {
        projectDefault()
    }
}
