plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":compiler:psi:psi-api"))
    api(project(":core:deserialization.common"))
    api(project(":core:deserialization.common.jvm"))
    api(project(":core:deserialization"))
    implementation(project(":analysis:decompiled:decompiler-to-stubs"))
    implementation(project(":compiler:frontend.common.jvm"))
    implementation(project(":core:compiler.common.jvm"))

    api(intellijCore())
}

sourceSets {
    "main" { projectDefault() }
}
