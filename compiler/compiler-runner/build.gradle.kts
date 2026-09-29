description = "Compiler runner + daemon client"

plugins {
    kotlin("jvm")
}

dependencies {
    embedded(project(":kotlin-compiler-runner-unshaded")) { isTransitive = false }

    api(project(":kotlin-daemon-client"))
    api(libs.kotlinx.coroutines.core) { isTransitive = false }

    runtimeOnly(project(":kotlin-compiler-embeddable"))
}

sourceSets {
    "main" { }
}

publish()

runtimeJar(rewriteDefaultJarDepsToShadedCompiler())
sourcesJar()
