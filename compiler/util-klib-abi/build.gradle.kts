plugins {
    kotlin("jvm")
}

dependencies {
    api(kotlinStdlib())
    implementation(project(":kotlin-util-klib"))
    implementation(project(":core:compiler.common"))
    implementation(project(":compiler:ir.serialization.common"))
    compileOnly(libs.intellij.fastutil)
}

sourceSets {
    "main" { projectDefault() }
}
