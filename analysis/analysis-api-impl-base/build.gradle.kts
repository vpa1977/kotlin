import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":compiler:psi:psi-api"))
    api(project(":analysis:analysis-api"))
    api(project(":analysis:analysis-api-platform-interface"))
    api(project(":analysis:kt-references"))
    api(project(":compiler:resolution.common.jvm"))
    implementation(project(":analysis:decompiled:decompiler-to-psi"))
    implementation(project(":compiler:backend"))
    implementation(project(":compiler:ir.backend.common"))
    implementation(kotlinxCollectionsImmutable())
    api(intellijCore())
    implementation(project(":analysis:analysis-internal-utils"))
    implementation(libs.caffeine)
}

sourceSets {
    "main" { projectDefault() }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.optIn.addAll(
        listOf(
            "org.jetbrains.kotlin.analysis.api.KaImplementationDetail",
            "org.jetbrains.kotlin.analysis.api.KaExperimentalApi",
            "org.jetbrains.kotlin.analysis.api.KaNonPublicApi",
            "org.jetbrains.kotlin.analysis.api.KaIdeApi",
            "org.jetbrains.kotlin.analysis.api.KaPlatformInterface",
            "org.jetbrains.kotlin.analysis.api.KaContextParameterApi",
            "org.jetbrains.kotlin.analysis.api.components.KaSessionComponentImplementationDetail",
            "org.jetbrains.kotlin.analysis.api.KaSpiExtensionPoint",
        )
    )
}
