import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":compiler:psi:psi-api"))
    implementation(project(":compiler:frontend.java"))
    implementation(project(":core:compiler.common"))
    implementation(project(":analysis:light-classes-base"))
    implementation(project(":compiler:backend.common.jvm"))
    implementation(project(":analysis:analysis-api-platform-interface"))
    implementation(project(":analysis:analysis-api"))
    implementation(project(":analysis:analysis-internal-utils"))
    implementation(project(":analysis:decompiled:light-classes-for-decompiled"))
    implementation(intellijCore())
    implementation(kotlinxCollectionsImmutable())
}

sourceSets {
    "main" { projectDefault() }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.optIn.addAll(
        "org.jetbrains.kotlin.analysis.api.permissions.KaAllowProhibitedAnalyzeFromWriteAction",
        "org.jetbrains.kotlin.analysis.api.KaExperimentalApi",
        "org.jetbrains.kotlin.analysis.api.KaPlatformInterface",
        "org.jetbrains.kotlin.analysis.api.KaSpiExtensionPoint",
    )
}
