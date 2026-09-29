plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core:descriptors"))
    api(project(":core:descriptors.jvm"))
    api(project(":core:deserialization"))
    api(project(":compiler:util"))
    api(project(":compiler:frontend"))
    api(project(":compiler:frontend.java"))
    api(project(":compiler:cli"))
    api(project(":compiler:cli-jvm"))
    api(project(":compiler:cli-js"))
    api(project(":compiler:cli-metadata"))
    api(project(":compiler:fir:entrypoint"))
    api(project(":compiler:fir:fir2ir:jvm-backend"))
    api(project(":compiler:ir.serialization.jvm"))
    api(project(":compiler:backend.jvm.entrypoint"))
    api(project(":kotlin-build-common"))
    api(project(":daemon-common"))
    api(project(":compiler:build-tools:kotlin-build-statistics"))
    api(project(":compiler:build-tools:kotlin-build-tools-api"))
    implementation(project(":compiler:build-tools:kotlin-build-tools-cri-impl"))
    compileOnly(intellijCore())
}

optInToK1Deprecation()

sourceSets {
    "main" { projectDefault() }
}
