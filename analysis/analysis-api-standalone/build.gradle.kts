plugins {
    kotlin("jvm")
}

dependencies {
    implementation(intellijCore())
    implementation(project(":compiler:psi:psi-api"))
    api(project(":analysis:analysis-api"))
    api(project(":analysis:analysis-api-platform-interface"))
    api(project(":analysis:analysis-api-fir"))
    api(project(":analysis:low-level-api-fir"))
    api(project(":analysis:symbol-light-classes"))
    api(project(":analysis:decompiled:light-classes-for-decompiled"))
    api(project(":analysis:analysis-api-standalone:analysis-api-standalone-base"))
    implementation(project(":analysis:analysis-api-standalone:analysis-api-fir-standalone-base"))
}

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("org.jetbrains.kotlin.analysis.api.KaPlatformInterface")
    }
}

sourceSets {
    "main" { projectDefault() }
}
