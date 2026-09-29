import plugins.KotlinBuildPublishingPlugin.Companion.ADHOC_COMPONENT_NAME

plugins {
    kotlin("jvm")
    `java-test-fixtures`
    `maven-publish`
}

configureKotlinCompileTasksGradleCompatibility()

kotlin.sourceSets.configureEach {
    languageSettings.optIn("org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi")
}

dependencies {
    val coreDepsVersion = libs.versions.kotlin.`for`.gradle.plugins.compilation.get()
    compileOnly(kotlin("stdlib", coreDepsVersion))
    api(project(":kotlin-tooling-core"))
    api(project(":kotlin-gradle-plugin-annotations"))

    testFixturesImplementation(gradleApi())
    testFixturesImplementation(gradleKotlinDsl())
    testFixturesImplementation(project(":kotlin-tooling-core"))
    testFixturesImplementation(project(":kotlin-gradle-plugin-idea-proto"))
    testFixturesImplementation(kotlin("stdlib", coreDepsVersion))
    testFixturesImplementation(kotlin("test", coreDepsVersion)) // no test annotations, only assertions are needed
}


publish(moduleMetadata = true) {
    fun ConfigurationVariantDetails.skipUnpublishable() {
        if (configurationVariant.artifacts.any { JavaBasePlugin.UNPUBLISHABLE_VARIANT_ARTIFACTS.contains(it.type) }) {
            skip()
        }
    }

    suppressAllPomMetadataWarnings()

    val kotlinLibraryComponent = components[ADHOC_COMPONENT_NAME] as AdhocComponentWithVariants

    kotlinLibraryComponent.addVariantsFromConfiguration(configurations.testFixturesApiElements.get()) {
        skipUnpublishable()
        mapToMavenScope("compile")
        mapToOptional()
    }

    kotlinLibraryComponent.addVariantsFromConfiguration(configurations.testFixturesRuntimeElements.get()) {
        skipUnpublishable()
        mapToMavenScope("runtime")
        mapToOptional()
    }
}

sourcesJar()
