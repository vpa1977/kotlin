plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":core:descriptors"))
    compileOnly(project(":core:descriptors.jvm"))
    compileOnly(project(":compiler:fir:cones"))
    compileOnly(project(":compiler:fir:resolve"))
    compileOnly(project(":compiler:fir:providers"))
    compileOnly(project(":compiler:fir:semantics"))
    compileOnly(project(":compiler:fir:tree"))
    compileOnly(project(":compiler:ir.tree"))
    compileOnly(project(":compiler:ir.backend.common"))
    compileOnly(project(":compiler:ir.serialization.common"))
    compileOnly(project(":compiler:fir:fir-serialization"))
    compileOnly(project(":compiler:fir:fir-deserialization"))
    compileOnly(project(":compiler:frontend.common.jvm"))
    compileOnly(project(":compiler:config.jvm"))
    compileOnly(project(":compiler:frontend"))
    compileOnly(project(":core:compiler.common.web"))

    compileOnly(intellijCore())
}

optInToObsoleteDescriptorBasedAPI()

sourceSets {
    "main" { projectDefault() }
}
