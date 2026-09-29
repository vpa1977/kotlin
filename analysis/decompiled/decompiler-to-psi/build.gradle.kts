plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":compiler:psi:psi-api"))
    implementation(project(":compiler:psi:psi-impl"))
    implementation(project(":compiler:frontend.java"))
    implementation(project(":core:compiler.common"))
    implementation(project(":analysis:light-classes-base"))
    implementation(project(":analysis:analysis-internal-utils"))
    implementation(project(":analysis:decompiled:decompiler-to-stubs"))
    implementation(project(":analysis:decompiled:decompiler-to-file-stubs"))
    implementation(project(":kotlin-util-klib-metadata"))
    implementation(intellijCore())
}

sourceSets {
    "main" { projectDefault() }
}
