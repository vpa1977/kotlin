plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":compiler:util"))
    api(project(":core:compiler.common"))
    api(project(":kotlin-stdlib"))
    api(project(":compiler:ir.tree"))
    implementation(project(":core:descriptors"))
    implementation(project(":core:util.runtime"))
    implementation(project(":kotlin-util-klib"))

    compileOnly(intellijCore())
}

optInToUnsafeDuringIrConstructionAPI()

sourceSets {
    "main" { projectDefault() }
}
