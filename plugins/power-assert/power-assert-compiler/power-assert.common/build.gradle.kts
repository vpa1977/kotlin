description = "Kotlin Power-Assert Compiler Plugin (Common)"

plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":compiler:frontend.common"))
    compileOnly(project(":compiler:frontend.common-psi"))
    compileOnly(intellijCore())
}

sourceSets {
    "main" { projectDefault() }
}

runtimeJar()
sourcesJar()
