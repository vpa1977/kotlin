plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":compiler:util"))
    api(project(":compiler:ir.tree"))
    api(project(":compiler:ir.interpreter"))
    api(project(":compiler:ir.serialization.common"))
    api(project(":compiler:ir.validation"))
    implementation(project(":core:compiler.common.native"))
    implementation(project(":compiler:frontend.common-psi")) // required for error reporting
    compileOnly(intellijCore())
}

optInToUnsafeDuringIrConstructionAPI()

sourceSets {
    "main" { projectDefault() }
}

