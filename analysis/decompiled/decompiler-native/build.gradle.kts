plugins {
    kotlin("jvm")
}

sourceSets {
    "main" { projectDefault() }
}

dependencies {
    api(project(":core:deserialization"))
    api(project(":compiler:psi:psi-api"))
    api(project(":compiler:frontend.java"))
    api(project(":analysis:decompiled:decompiler-to-file-stubs"))
    api(project(":analysis:decompiled:decompiler-to-psi"))
    api(project(":analysis:decompiled:decompiler-to-stubs"))
    api(project(":kotlin-util-klib-metadata"))

    implementation(project(":js:js.serializer"))

    compileOnly(intellijCore())
}

optInToK1Deprecation()
