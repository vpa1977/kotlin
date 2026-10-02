buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-build-gradle-plugin:${kotlinBuildProperties.buildGradlePluginVersion.get()}")
    }

    /**
     * Global Security Fixes for Common Dependencies
     *
     * Enforces minimum secure versions for commonly used libraries across all subprojects.
     * These overrides address known vulnerabilities in transitive dependencies that might
     * be pulled in by various subprojects.
     *
     * Affected Libraries:
     * └── org.apache.commons
     *     ├── commons-compress:* → 1.27.1
     *     ├── commons-io:* → 2.18.0
     *     └── commons-lang3:* → 3.18.0
     *
     * Mitigated Vulnerabilities:
     * 1. Commons Compress
     *    - CVE-2024-26308: Potential security vulnerability
     *    - CVE-2024-25710: Input validation weakness
     *    - CVE-2023-42503: Potential code execution risk
     *
     * 2. Commons IO
     *    - CVE-2024-26308: Security vulnerability
     *    - CVE-2023-42503: Input processing risk
     *
     * 3. Commons Lang
     *    - CVE-2025-48924: Uncontrolled Recursion vulnerability
     */
    configurations.all {
        resolutionStrategy.eachDependency {
            // Apache Commons libraries
            if (requested.group == "org.apache.commons" && requested.name == "commons-compress") {
                useVersion(libs.versions.commons.compress.get())
                because("CVE-2024-26308, CVE-2024-25710, CVE-2023-42503")
            }
            if (requested.group == "commons-io" && requested.name == "commons-io") {
                useVersion(libs.versions.commons.io.get())
                because("CVE-2024-26308, CVE-2023-42503")
            }
            if (requested.group == "org.apache.commons" && requested.name == "commons-lang3") {
                useVersion(libs.versions.commons.lang.get())
                because("CVE-2025-48924")
            }
        }
    }
}

plugins {
    base
    id("build-time-report")
    id("java-instrumentation")
    id("modularized-test-configurations")
    id("resolve-dependencies")
    id("org.gradle.crypto.checksum") version "1.4.0"
    alias(libs.plugins.kotlinx.bcv) apply false
    id("signing-convention")
    id("org.jetbrains.kotlin.jvm") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false
    if (kotlinBuildProperties.isKotlinNativeEnabled.get()) {
        id("kotlin.native.build-tools-conventions") apply false
    }
    `jvm-toolchains`
    id("com.autonomousapps.dependency-analysis") version "3.6.1"
    id("project-tests-convention") apply false
    id("test-data-manager-root")
}

val isTeamcityBuild = project.kotlinBuildProperties.isTeamcityBuild

val defaultSnapshotVersion: String by extra
findProperty("deployVersion")?.let {
    assert(findProperty("build.number") != null) { "`build.number` parameter is expected to be explicitly set with the `deployVersion`" }
}
val buildNumber by extra(findProperty("build.number")?.toString() ?: defaultSnapshotVersion)
val kotlinVersion by extra(
    findProperty("deployVersion")?.toString()?.let { deploySnapshotStr ->
        if (deploySnapshotStr != "default.snapshot") deploySnapshotStr else defaultSnapshotVersion
    } ?: buildNumber
)

val kotlinLanguageVersion: String by extra
val kotlinApiVersionForModulesUsedInIDE: String by extra

extra["kotlin_root"] = rootDir

val jpsBootstrap by configurations.creating

val commonBuildDir = File(rootDir, "build")
val distDir by extra("$rootDir/dist")
val distKotlinHomeDir by extra("$distDir/kotlinc")
val distLibDir = "$distKotlinHomeDir/lib"
val commonLocalDataDir = "$rootDir/local"
val ideaSandboxDir = "$commonLocalDataDir/ideaSandbox"
val artifactsDir = "$distDir/artifacts"
val ideaPluginDir = "$artifactsDir/ideaPlugin/Kotlin"

// TODO: use "by extra()" syntax where possible
extra["distLibDir"] = project.file(distLibDir)
extra["commonLocalDataDir"] = project.file(commonLocalDataDir)
extra["ideaSandboxDir"] = project.file(ideaSandboxDir)
extra["ideaPluginDir"] = project.file(ideaPluginDir)

rootProject.apply {
    from(rootProject.file("gradle/versions.gradle.kts"))
    from(rootProject.file("gradle/checkArtifacts.gradle.kts"))
    from(rootProject.file("gradle/checkCacheability.gradle.kts"))
    from(rootProject.file("gradle/retryPublishing.gradle.kts"))
}

IdeVersionConfigurator.setCurrentIde(project)

if (!project.hasProperty("versions.kotlin-native")) {
    /**
     * "versions.kotlin-native" is the version of K/N dist that will be baked into KGP and that KGP will try to resolve to run K/N
     * compilations (including in KGP tests).
     */
    extra["versions.kotlin-native"] = if (kotlinBuildProperties.alignKotlinNativeVersionInTCBuilds) {
        kotlinVersion
    } else if (kotlinBuildProperties.isKotlinNativeEnabled.get()) {
        kotlinBuildProperties.defaultSnapshotVersion.get()
    } else {
        "2.4.0-dev-6762"
    }
}

val irCompilerModules = arrayOf(
    ":compiler:ir.tree",
    ":compiler:ir.serialization.common",
    ":compiler:ir.serialization.jvm",
    ":compiler:ir.serialization.native",
    ":compiler:ir.objcinterop",
    ":compiler:ir.backend.common",
    ":compiler:ir.backend.native",
    ":compiler:ir.actualization",
    ":compiler:ir.interpreter",
    ":compiler:ir.inline",
    ":compiler:ir.validation",
).also { extra["irCompilerModules"] = it }

val irCompilerModulesForIDE = arrayOf(
    ":compiler:ir.tree",
    ":compiler:ir.serialization.common",
    ":compiler:ir.serialization.jvm",
    ":compiler:ir.serialization.js", // used in IJ android plugin in `ComposeIrGenerationExtension`
    ":compiler:ir.objcinterop",
    ":compiler:ir.backend.common",
    ":compiler:ir.backend.native",
    ":compiler:ir.actualization",
    ":compiler:ir.interpreter",
    ":compiler:ir.inline",
    ":compiler:ir.validation",
).also { extra["irCompilerModulesForIDE"] = it }

val commonCompilerModules = arrayOf(
    ":compiler:cli-base",
    ":compiler:cli",
    ":compiler:cli-jvm",
    ":compiler:cli-metadata",
    ":compiler:psi:psi-api",
    ":compiler:psi:psi-impl",
    ":compiler:psi:psi-utils",
    ":compiler:psi:psi-frontend-utils",
    ":compiler:psi:parser",
    ":compiler:frontend.common-psi",
    ":compiler:frontend.common",
    ":compiler:util",
    ":compiler:config",
    ":compiler:config.jvm",
    ":compiler:compiler.version",
    ":compiler:arguments.common",
    ":compiler:resolution.common",
    ":compiler:resolution.common.jvm",
    ":compiler:backend.common.jvm",
    ":compiler:plugin-api",
    ":core:metadata",
    ":core:metadata.jvm",
    ":core:deserialization.common",
    ":core:deserialization.common.jvm",
    ":core:compiler.common",
    ":core:compiler.common.jvm",
    ":core:compiler.common.native",
    ":core:util.runtime",
    ":core:names",
    ":core:language.model",
    ":core:language.targets",
    ":core:language.targets.jvm",
    ":core:language.version-settings",
    ":compiler:frontend.common.jvm",
    ":compiler:frontend.java", // TODO this is fe10 module but some utils used in fir ide now
    ":analysis:analysis-internal-utils",
    ":analysis:light-classes-base",
    ":analysis:decompiled:decompiler-to-stubs",
    ":analysis:decompiled:decompiler-to-file-stubs",
    ":analysis:decompiled:decompiler-native",
    ":analysis:decompiled:decompiler-to-psi",
    ":analysis:decompiled:light-classes-for-decompiled",
    ":analysis:kt-references",
    ":kotlin-build-common",
    ":kotlin-util-io",
    ":kotlin-util-klib",
    ":kotlin-util-klib-abi",
    ":native:base",
    ":native:binary-options",
    ":native:kotlin-native-utils",
    ":compiler:build-tools:kotlin-build-statistics",
    ":native:native.config",
).also { extra["commonCompilerModules"] = it }

val firCompilerCoreModules = arrayOf(
    ":compiler:fir:cones",
    ":compiler:fir:providers",
    ":compiler:fir:semantics",
    ":compiler:fir:resolve",
    ":compiler:fir:fir-serialization",
    ":compiler:fir:fir-deserialization",
    ":compiler:fir:plugin-utils",
    ":compiler:fir:tree",
    ":compiler:fir:fir-jvm",
    ":compiler:fir:fir-native",
    ":compiler:fir:raw-fir:raw-fir.common",
    ":compiler:fir:raw-fir:psi2fir",
    ":compiler:fir:checkers",
    ":compiler:fir:checkers:checkers.jvm",
    ":compiler:fir:checkers:checkers.native",
    ":compiler:fir:diagnostic-renderers",
    ":compiler:fir:entrypoint", // TODO should not be in core modules but FIR IDE uses DependencyListForCliModule from this module
    ":compiler:fir:fir2ir:jvm-backend",  // TODO should not be in core modules but FIR IDE uses Fir2IrSignatureComposer from this module
    ":compiler:fir:fir2ir" // TODO should not be in core modules but FIR IDE uses Fir2IrSignatureComposer from this module
).also { extra["firCompilerCoreModules"] = it }

val firAllCompilerModules: Array<String> = (
        firCompilerCoreModules + arrayOf(
            ":compiler:fir:raw-fir:light-tree2fir",
            ":compiler:fir:analysis-tests",
            ":compiler:fir:analysis-tests:legacy-fir-tests"
        )
        ).also { extra["firAllCompilerModules"] = it }

val fe10CompilerModules = arrayOf(
    ":compiler",
    ":core:descriptors.runtime",
    ":core:descriptors",
    ":core:descriptors.jvm",
    ":compiler:light-classes",
    ":compiler:resolution",
    ":compiler:serialization",
    ":compiler:frontend",
    ":compiler:container",
    ":core:deserialization",
    ":compiler:frontend:cfg",
    ":compiler:ir.psi2ir",
    ":compiler:backend.jvm",
    ":compiler:backend.jvm.lower",
    ":compiler:backend.jvm.codegen",
    ":compiler:backend.jvm.entrypoint",
    ":kotlin-util-klib-metadata",
    ":compiler:backend",
    ":compiler:javac-wrapper",
    ":compiler:incremental-compilation-impl",
    ":native:frontend.native",
    ":compiler:backend.common.jvm",
).also { extra["fe10CompilerModules"] = it }

extra["compilerModules"] =
    irCompilerModules +
            fe10CompilerModules +
            commonCompilerModules +
            firAllCompilerModules

/**
 * An array of projects used in the IntelliJ Kotlin Plugin.
 *
 * Experimental declarations from Kotlin stdlib cannot be used in those projects to avoid stdlib binary compatibility problems.
 * See KT-62510 for details.
 */
val projectsUsedInIntelliJKotlinPlugin =
    fe10CompilerModules +
            commonCompilerModules +
            firCompilerCoreModules +
            irCompilerModulesForIDE +
            arrayOf(
                ":analysis:analysis-api",
                ":analysis:analysis-api-fe10",
                ":analysis:analysis-api-fir",
                ":analysis:analysis-api-impl-base",
                ":analysis:analysis-api-platform-interface",
                ":analysis:analysis-api-standalone:analysis-api-standalone-base",
                ":analysis:analysis-api-standalone:analysis-api-fir-standalone-base",
                ":analysis:analysis-api-standalone",
                ":analysis:analysis-test-framework",
                ":analysis:decompiled",
                ":analysis:kt-references",
                ":analysis:light-classes-base",
                ":analysis:low-level-api-fir",
                ":analysis:stubs",
                ":analysis:symbol-light-classes",
            ) +
            arrayOf(
                ":kotlin-allopen-compiler-plugin.cli",
                ":kotlin-allopen-compiler-plugin.common",
                ":kotlin-allopen-compiler-plugin.k1",
                ":kotlin-allopen-compiler-plugin.k2",

                ":kotlin-assignment-compiler-plugin.cli",
                ":kotlin-assignment-compiler-plugin.common",
                ":kotlin-assignment-compiler-plugin.k1",
                ":kotlin-assignment-compiler-plugin.k2",

                ":plugins:parcelize:parcelize-compiler:parcelize.backend",
                ":plugins:parcelize:parcelize-compiler:parcelize.cli",
                ":plugins:parcelize:parcelize-compiler:parcelize.common",
                ":plugins:parcelize:parcelize-compiler:parcelize.k1",
                ":plugins:parcelize:parcelize-compiler:parcelize.k2",
                ":plugins:parcelize:parcelize-runtime",

                ":plugins:compose-compiler-plugin:compiler-hosted",

                ":kotlin-sam-with-receiver-compiler-plugin.cli",
                ":kotlin-sam-with-receiver-compiler-plugin.common",
                ":kotlin-sam-with-receiver-compiler-plugin.k1",
                ":kotlin-sam-with-receiver-compiler-plugin.k2",

                ":kotlinx-serialization-compiler-plugin.cli",
                ":kotlinx-serialization-compiler-plugin.common",
                ":kotlinx-serialization-compiler-plugin.k1",
                ":kotlinx-serialization-compiler-plugin.k2",
                ":kotlinx-serialization-compiler-plugin.backend",

                ":plugins:js-plain-objects:compiler-plugin:js-plain-objects.cli",
                ":plugins:js-plain-objects:compiler-plugin:js-plain-objects.common",
                ":plugins:js-plain-objects:compiler-plugin:js-plain-objects.k2",
                ":plugins:js-plain-objects:compiler-plugin:js-plain-objects.backend",

                ":kotlin-lombok-compiler-plugin.cli",
                ":kotlin-lombok-compiler-plugin.common",
                ":kotlin-lombok-compiler-plugin.k1",
                ":kotlin-lombok-compiler-plugin.k2",

                ":kotlin-noarg-compiler-plugin.cli",
                ":kotlin-noarg-compiler-plugin.common",
                ":kotlin-noarg-compiler-plugin.k1",
                ":kotlin-noarg-compiler-plugin.k2",
                ":kotlin-noarg-compiler-plugin.backend",

                ":kotlin-sam-with-receiver-compiler-plugin.cli",
                ":kotlin-sam-with-receiver-compiler-plugin.common",
                ":kotlin-sam-with-receiver-compiler-plugin.k1",
                ":kotlin-sam-with-receiver-compiler-plugin.k2",

                ":kotlin-dataframe-compiler-plugin.cli",
                ":kotlin-dataframe-compiler-plugin.common",
                ":kotlin-dataframe-compiler-plugin.k2",
                ":kotlin-dataframe-compiler-plugin.backend",

                ":kotlin-compiler-runner-unshaded",
                ":kotlin-preloader",
                ":daemon-common",
                ":kotlin-daemon-client",

                ":kotlin-scripting-jvm",

                ":kotlin-scripting-compiler",
                ":kotlin-gradle-statistics",
                ":jps:jps-common",
                ":compiler:build-tools:kotlin-build-tools-api",
            ) +
            arrayOf(
                ":compiler:ir.serialization.native",
                ":libraries:tools:analysis-api-based-klib-reader",
                ":native:base",
                ":native:objcexport-header-generator",
                ":native:objcexport-header-generator-analysis-api",
                ":native:objcexport-header-generator-k1",
                ":native:analysis-api-based-export-common",
            ) +
            arrayOf(
                ":native:swift:sir",
                ":native:swift:sir-light-classes",
                ":native:swift:sir-printer",
                ":native:swift:sir-providers",
                ":native:swift:swift-export-ide",
            ) +
            arrayOf(
                ":analysis:analysis-tools:deprecated-k1-frontend-internals-for-ide-generated",
            )

extra["projectsUsedInIntelliJKotlinPlugin"] = projectsUsedInIntelliJKotlinPlugin

// They are embedded just because we don't publish those dependencies as separate Maven artifacts (yet)
extra["kotlinJpsPluginEmbeddedDependencies"] = listOf(
    ":compiler:cli-base",
    ":kotlin-build-tools-enum-compat",
    ":kotlin-compiler-runner-unshaded",
    ":daemon-common",
    ":core:names",
    ":core:language.model",
    ":core:language.targets",
    ":core:language.targets.jvm",
    ":core:language.version-settings",
    ":core:compiler.common",
    ":core:compiler.common.jvm",
    ":core:compiler.common.js",
    ":core:compiler.common.native",
    ":core:compiler.common.wasm",
    ":core:compiler.common.web",
    ":core:descriptors",
    ":core:descriptors.jvm",
    ":compiler:backend.common.jvm",
    ":js:js.serializer",
    ":core:deserialization",
    ":core:deserialization.common",
    ":core:deserialization.common.jvm",
    ":compiler:frontend.common.jvm",
    ":compiler:frontend.java",
    ":core:metadata",
    ":core:metadata.jvm",
    ":kotlin-preloader",
    ":compiler:util",
    ":compiler:config",
    ":compiler:config.jvm",
    ":js:js.config",
    ":wasm:wasm.config",
    ":native:native.config",
    ":core:util.runtime",
    ":compiler:compiler.version",
    ":compiler:build-tools:kotlin-build-statistics",
    ":kotlin-build-common",
    ":compiler:arguments.common",
)

extra["kotlinJpsPluginMavenDependencies"] = listOf(
    ":kotlin-daemon-client",
    ":kotlin-util-io",
    ":kotlin-util-klib",
    ":kotlin-util-klib-metadata",
    ":native:kotlin-native-utils",
    ":compiler:build-tools:kotlin-build-tools-api",
)

extra["kotlinJpsPluginMavenDependenciesNonTransitiveLibs"] = listOf(
    commonDependency("org.jetbrains.kotlin:kotlin-reflect")
)

extra["compilerArtifactsForIde"] = listOfNotNull(
    ":prepare:ide-plugin-dependencies:allopen-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:compose-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:js-plain-objects-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:scripting-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:incremental-compilation-impl-tests-for-ide",
    ":prepare:ide-plugin-dependencies:js-ir-runtime-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-build-common-tests-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-cli-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-gradle-statistics-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-jps-common-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-jps-plugin-classpath",
    ":prepare:ide-plugin-dependencies:kotlin-jps-plugin-tests-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-jps-plugin-testdata-for-ide",
    ":prepare:ide-plugin-dependencies:kotlinx-serialization-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:noarg-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:sam-with-receiver-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:assignment-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:parcelize-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:lombok-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-dataframe-compiler-plugin-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-objcexport-header-generator-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-swift-export-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-tests-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-testdata-for-ide",
    ":prepare:ide-plugin-dependencies:low-level-api-fir-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-impl-base-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-impl-base-tests-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-k2-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-k2-tests-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-fe10-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-fe10-tests-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-platform-interface-for-ide",
    ":prepare:ide-plugin-dependencies:symbol-light-classes-for-ide",
    ":prepare:ide-plugin-dependencies:analysis-api-standalone-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-ir-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-common-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-fe10-for-ide",
    ":prepare:ide-plugin-dependencies:kotlin-compiler-fir-for-ide",
    ":prepare:kotlin-jps-plugin",
    ":kotlin-script-runtime",
    ":kotlin-scripting-common",
    ":kotlin-scripting-dependencies",
    ":kotlin-scripting-jvm",
    ":kotlin-scripting-compiler",
    ":kotlin-scripting-compiler-impl",
    ":plugins:parcelize:parcelize-runtime",
    ":plugins:jvm-abi-gen",
    ":kotlin-stdlib-common",
    ":kotlin-stdlib",
    ":kotlin-test",
    ":kotlin-daemon",
    ":kotlin-compiler",
    ":kotlin-annotations-jvm",
    ":kotlin-stdlib-jdk7",
    ":kotlin-stdlib-jdk8",
    ":kotlin-reflect",
    ":kotlin-main-kts",
    ":kotlin-dom-api-compat",
    ":compiler:build-tools:kotlin-build-tools-api",
    ":compiler:build-tools:kotlin-build-tools-impl",
    ":compiler:build-tools:kotlin-build-tools-cri-impl",
)

val coreLibProjects by extra {
    listOfNotNull(
        ":kotlin-stdlib",
        ":kotlin-stdlib-jdk7",
        ":kotlin-stdlib-jdk8",
        ":kotlin-test",
        ":kotlin-reflect",
        ":kotlin-metadata-jvm",
    )
}
val mppProjects by extra {
    listOf(
        ":kotlin-stdlib",
        ":kotlin-test",
    )
}

val projectsWithOptInToUnsafeCastFunctionsFromAddToStdLib by extra {
    listOf(
        ":analysis:analysis-api-fe10",
        ":analysis:analysis-api-fir",
        ":analysis:decompiled:light-classes-for-decompiled",
        ":analysis:symbol-light-classes",
        ":compiler",
        ":compiler:backend.js",
        ":compiler:light-classes",
        ":jps:jps-common",
        ":js:js.tests",
        ":kotlin-build-common",
        ":kotlin-gradle-plugin",
        ":kotlin-scripting-jvm-host-test",
        ":native:kotlin-klib-commonizer",
    )
}

val gradlePluginProjects = listOf(
    ":kotlin-gradle-plugin",
    ":kotlin-gradle-plugin-api",
    ":kotlin-gradle-plugin-annotations",
    ":kotlin-gradle-plugin-idea",
    ":kotlin-gradle-plugin-idea-proto",
    ":kotlin-gradle-plugin-tcs-android",
    ":compose-compiler-gradle-plugin",
    ":kotlin-allopen",
    ":kotlin-noarg",
    ":kotlin-power-assert",
    ":kotlin-sam-with-receiver",
    ":kotlin-parcelize-compiler",
    ":kotlin-lombok",
    ":kotlin-assignment",
    ":kotlin-dataframe"
)

val ignoreTestFailures by extra(project.kotlinBuildProperties.ignoreTestFailures)

val dependencyOnSnapshotReflectWhitelist = setOf(
    ":kotlin-compiler",
    ":kotlin-reflect",
    ":tools:binary-compatibility-validator",
    ":tools:kotlin-stdlib-gen",
)

allprojects {
    if (!project.path.startsWith(":kotlin-ide.")) {
        pluginManager.apply("common-configuration")
    }
    if (!project.path.startsWith(":compiler:build-tools")) {
        pluginManager.apply("com.autonomousapps.dependency-analysis")
    }
    if (kotlinBuildProperties.isInIdeaSync.get()) {
        afterEvaluate {
            configurations.all {
                // Remove kotlin-compiler from dependencies during Idea import. KTI-1598
                if (dependencies.removeIf { (it as? ProjectDependency)?.path == ":kotlin-compiler" }) {
                    logger.warn("Removed :kotlin-compiler project dependency from $this")
                }
            }
        }
    }

    configurations.all {
        val configuration = this
        if (name != "compileClasspath") {
            return@all
        }
        resolutionStrategy.eachDependency {
            if (requested.group != "org.jetbrains.kotlin") {
                return@eachDependency
            }

            val isReflect = requested.name == "kotlin-reflect"
            // More strict check for "compilerModules". We can't apply this check for all modules because it would force to
            // exclude kotlin-reflect from transitive dependencies of kotlin-poet, ktor, com.android.tools.build:gradle, etc
            if (project.path in @Suppress("UNCHECKED_CAST") (rootProject.extra["compilerModules"] as Array<String>)) {
                val expectedReflectVersion = commonDependencyVersion("org.jetbrains.kotlin", "kotlin-reflect")
                if (isReflect) {
                    check(requested.version == expectedReflectVersion) {
                        """
                            $configuration: 'kotlin-reflect' should have '$expectedReflectVersion' version. But it was '${requested.version}'
                            Suggestions:
                                1. Use 'commonDependency("org.jetbrains.kotlin:kotlin-reflect") { isTransitive = false }'
                                2. Avoid 'kotlin-reflect' leakage from transitive dependencies with 'exclude("org.jetbrains.kotlin")'
                        """.trimIndent()
                    }
                }
                if (requested.name.startsWith("kotlin-stdlib")) {
                    check(requested.version != expectedReflectVersion) {
                        """
                            $configuration: '${requested.name}' has a wrong version. It's not allowed to be '$expectedReflectVersion'
                            Suggestions:
                                1. Most likely, it leaked from 'kotlin-reflect' transitive dependencies. Use 'isTransitive = false' for
                                   'kotlin-reflect' dependencies
                                2. Avoid '${requested.name}' leakage from other transitive dependencies with 'exclude("org.jetbrains.kotlin")'
                        """.trimIndent()
                    }
                }
            }
            if (isReflect && project.path !in dependencyOnSnapshotReflectWhitelist) {
                check(requested.version != kotlinVersion) {
                    """
                        $configuration: 'kotlin-reflect' is not allowed to have '$kotlinVersion' version.
                        Suggestion: Use 'commonDependency("org.jetbrains.kotlin:kotlin-reflect") { isTransitive = false }'
                    """.trimIndent()
                }
            }
        }
    }
    val mirrorRepo: String? = findProperty("maven.repository.mirror")?.toString()

    repositories {
        when (kotlinBuildProperties.stringProperty("attachedIntellijVersion").orNull) {
            null -> {}
            "master" -> {
                maven { setUrl("https://www.jetbrains.com/intellij-repository/snapshots") }
            }

            else -> {
                kotlinBuildLocalRepo(project)
            }
        }

        mirrorRepo?.let(::maven)

        maven(intellijRepo) {
            content {
                includeGroupByRegex("com\\.jetbrains\\.intellij(\\..+)?")
            }
        }

        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies") {
            content {
                includeGroupByRegex("org\\.jetbrains\\.intellij\\.deps(\\..+)?")
                includeGroupByRegex("com.intellij.platform.*")
                includeGroupByRegex("org.jetbrains.jps.*")
                includeVersion("org.jetbrains.jps", "jps-javac-extension", "7")
                includeVersion("com.google.protobuf", "protobuf-parent", "3.24.4-jb.2")
                includeVersion("com.google.protobuf", "protobuf-java", "3.24.4-jb.2")
                includeVersion("com.google.protobuf", "protobuf-bom", "3.24.4-jb.2")
                includeModuleByRegex("org\\.jetbrains", "(syntax\\-api|lang\\-syntax).*")
            }
        }

        maven("https://redirector.kotlinlang.org/maven/kotlin-dependencies") {
            content {
                includeModule("org.jetbrains.dukat", "dukat")
                includeModule("org.jetbrains.kotlin", "android-dx")
                includeModule("org.jetbrains.kotlin", "jcabi-aether")
                includeModule("org.jetbrains.kotlin", "kotlin-build-gradle-plugin")
                includeModule("org.jetbrains.kotlin", "protobuf-lite")
                includeModule("org.jetbrains.kotlin", "protobuf-relocated")
                includeModule("org.jetbrains.kotlinx", "kotlinx-metadata-klib")
            }
        }

        maven("https://download.jetbrains.com/teamcity-repository") {
            content {
                includeModule("org.jetbrains.teamcity", "serviceMessages")
                includeModule("org.jetbrains.teamcity.idea", "annotations")
            }
        }

        maven("https://dl.google.com/dl/android/maven2") {
            content {
                includeGroup("com.android.tools")
                includeGroup("com.android.tools.build")
                includeGroup("com.android.tools.layoutlib")
                includeGroup("com.android")
                includeGroup("androidx.test")
                includeGroup("androidx.annotation")
            }
        }

        mavenCentral()
    }
}

gradle.taskGraph.whenReady {
    fun Boolean.toOnOff(): String = if (this) "on" else "off"
    val profile = if (isTeamcityBuild.get()) "CI" else "Local"

    val proguardMessage = "proguard is ${kotlinBuildProperties.proguard.toOnOff()}"
    val jarCompressionMessage = "jar compression is ${kotlinBuildProperties.jarCompression.toOnOff()}"

    logger.warn(
        "$profile build profile is active ($proguardMessage, $jarCompressionMessage). " +
                "Use -Pteamcity=<true|false> to reproduce CI/local build"
    )

    allTasks.filterIsInstance<org.gradle.jvm.tasks.Jar>().forEach { task ->
        task.entryCompression = if (kotlinBuildProperties.jarCompression)
            ZipEntryCompression.DEFLATED
        else
            ZipEntryCompression.STORED
    }
}

// KotlinBuildPublishingPlugin wires every published project's `install` task
// into this root lifecycle task.
tasks.register("mvnInstall") {
    group = "publishing"
}

afterEvaluate {
    checkExpectedGradlePropertyValues()
}