import GenerateKgpBuildConstantsTask.Companion.registerGenerateKgpBuildConstantsTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gradle.GradlePluginVariant
import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("gradle-plugin-common-configuration")
    id("asm-deprecating-transformer")
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    compilerOptions {
        optIn.addAll(
            listOf(
                "kotlin.RequiresOptIn",
                "org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi",
                "org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi",
                "org.jetbrains.kotlin.gradle.ExternalKotlinTargetApi",
                "org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi",
                "org.jetbrains.kotlin.gradle.ComposeKotlinGradlePluginApi",
                "org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl",
                "org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation",
            )
        )
    }
}

registerKotlinSourceForVersionRange(
    GradlePluginVariant.GRADLE_MIN,
    GradlePluginVariant.GRADLE_82,
)

registerKotlinSourceForVersionRange(
    GradlePluginVariant.GRADLE_MIN,
    GradlePluginVariant.GRADLE_86,
)

registerKotlinSourceForVersionRange(
    GradlePluginVariant.GRADLE_MIN,
    GradlePluginVariant.GRADLE_811,
)

val unpublishedCompilerRuntimeDependencies = listOf(
    // TODO: remove in KT-70247
    ":compiler:cli", // for MessageRenderer, related to MessageCollector usage
    ":compiler:cli-base", // for compiler arguments setup, for logging via MessageCollector, CompilerSystemProperties, ExitCode
    ":compiler:arguments.common", // for compiler arguments parser setup (using `@Enables`, `@Disables` and other annotations)
    ":compiler:compiler.version", // for user projects buildscripts, `loadCompilerVersion`
    ":compiler:config", // for CommonCompilerArguments initialization
    ":compiler:config.jvm", // for K2JVMCompilerArguments initialization
    ":compiler:ir.serialization.common", // for PartialLinkageMode (K/N)
    ":compiler:util", // for CommonCompilerArguments initialization, K/N
    ":core:compiler.common", // for FUS statistics parsing all the compiler arguments
    ":core:compiler.common.jvm", // for FUS statistics parsing all the compiler arguments
    ":core:descriptors", // for `fromUIntToLong`
    ":core:util.runtime", // for stdlib extensions
    ":core:language.model", ":core:language.targets", ":core:language.targets.jvm", // For JvmTarget
    ":core:language.version-settings", // For LanguageFeature
    ":core:names", // For ClassId
    ":kotlin-build-common", // for incremental compilation setup
    ":js:js.config", // for k/js task
    ":wasm:wasm.config", // for k/js task
)

dependencies {
    commonApi(platform(project(":kotlin-gradle-plugins-bom")))
    commonApi(project(":kotlin-gradle-plugin-api"))
    commonApi(project(":libraries:tools:gradle:fus-statistics-gradle-plugin"))

    for (compilerRuntimeDependency in unpublishedCompilerRuntimeDependencies) {
        commonCompileOnly(project(compilerRuntimeDependency)) { isTransitive = false }
    }
    commonCompileOnly(libs.guava)
    commonCompileOnly(project(":daemon-common")) {
        isTransitive = false
    }
    commonCompileOnly(project(":kotlin-daemon-client")) {
        isTransitive = false
    }
    commonCompileOnly(project(":kotlin-gradle-compiler-types"))
    commonCompileOnly(project(":kotlin-compiler-runner-unshaded")) {
        isTransitive = false
    }
    commonCompileOnly(project(":kotlin-gradle-statistics"))
    commonCompileOnly(project(":kotlin-gradle-build-metrics"))
    commonCompileOnly(project(":compiler:build-tools:kotlin-build-tools-jdk-utils"))
    commonCompileOnly(libs.android.gradle.plugin.gradle.api) {
        overrideTargetJvmVersion(11)
        isTransitive = false
    }
    commonCompileOnly(libs.android.gradle.plugin.gradle) {
        overrideTargetJvmVersion(11)
        isTransitive = false
    }
    commonCompileOnly(libs.android.gradle.plugin.builder) {
        overrideTargetJvmVersion(11)
        isTransitive = false
    }
    commonCompileOnly(libs.android.gradle.plugin.builder.model) {
        overrideTargetJvmVersion(11)
        isTransitive = false
    }
    commonCompileOnly(libs.android.tools.common) {
        overrideTargetJvmVersion(11)
        isTransitive = false
    }
    commonCompileOnly(commonDependency("org.jetbrains.teamcity:serviceMessages"))
    commonCompileOnly(libs.develocity.gradlePlugin)
    commonCompileOnly(commonDependency("com.google.code.gson:gson"))
    commonCompileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json") {
        version {
            strictly(GradlePluginVariant.GRADLE_MIN.compatibleKotlinxJsonSerializationVersion)
        }
    }
    commonCompileOnly("com.github.gundy:semver4j:0.16.4:nodeps") {
        exclude(group = "*")
    }
    commonCompileOnly(project(":kotlin-tooling-metadata"))
    commonCompileOnly(project(":compiler:build-tools:kotlin-build-statistics"))
    commonCompileOnly(project(":native:swift:swift-export-standalone"))
    commonCompileOnly(libs.intellij.asm) { isTransitive = false }

    commonCompileOnly(libs.develocity.gradlePluginAdapter)

    commonImplementation(project(":kotlin-gradle-plugin-idea"))
    commonImplementation(project(":kotlin-gradle-plugin-idea-proto"))
    commonImplementation(project(":native:kotlin-klib-commonizer-api")) // TODO: consider removing in KT-70247

    commonImplementation(project(":compiler:build-tools:kotlin-build-statistics"))
    commonImplementation(project(":kotlin-util-klib-metadata")) // TODO: consider removing in KT-70247

    commonRuntimeOnly(project(":kotlin-compiler-runner")) { // TODO: consider removing in KT-70247
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    for (compilerRuntimeDependency in unpublishedCompilerRuntimeDependencies) {
        embedded(project(compilerRuntimeDependency)) { isTransitive = false }
    }

    embedded(project(":kotlin-gradle-build-metrics"))
    embedded(project(":kotlin-gradle-statistics"))
    embedded(libs.intellij.asm) { isTransitive = false }
    embedded(commonDependency("com.google.code.gson:gson")) { isTransitive = false }
    embedded(libs.develocity.gradlePluginAdapter)
    embedded("org.jetbrains.kotlinx:kotlinx-serialization-json") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
        version {
            strictly(GradlePluginVariant.GRADLE_MIN.compatibleKotlinxJsonSerializationVersion)
        }
    }
    embedded(libs.guava) { isTransitive = false }
    embedded(libs.guava.failureaccess) { isTransitive = false }
    embedded(commonDependency("org.jetbrains.teamcity:serviceMessages")) { isTransitive = false }
    embedded(project(":kotlin-tooling-metadata")) { isTransitive = false }
    embedded("com.github.gundy:semver4j:0.16.4:nodeps") {
        exclude(group = "*")
    }

    commonCompileOnly(libs.apache.commons.compress)
    embedded(libs.apache.commons.compress)

    // Adding workaround KT-57317 for Gradle versions where Kotlin runtime <1.8.0
    "mainEmbedded"(project(":kotlin-build-tools-enum-compat"))

    commonCompileOnly(libs.bouncycastle.bcpkix.jdk18on)
    commonCompileOnly(libs.bouncycastle.bcpg.jdk18on)
}

configurations.commonCompileClasspath.get().exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")

/**
 * Security Advisory: Vulnerable Transitive Dependencies
 *
 * The dependency com.android.tools.build:gradle:8.8.1 introduces several transitive
 * dependencies with known security vulnerabilities. The following configuration
 * enforces safer versions of these dependencies.
 *
 * Affected Libraries:
 * ├── com.google.protobuf
 * │   ├── protobuf-java:* → 3.25.5
 * │   └── protobuf-java-util:3.22.3
 * ├── io.netty
 * │   ├── netty-buffer:*
 * │   ├── netty-codec-http:* → 4.1.127.Final
 * │   ├── netty-codec-http2:* → 4.1.127.Final
 * │   ├── netty-common:* → 4.1.127.Final
 * │   └── netty-handler:* → 4.1.127.Final
 * ├── org.apache.commons
 * │   ├── commons-compress:* → 1.27.1
 * │   └── commons-io:* → 2.16.1
 * └── org.bouncycastle:bcpkix-jdk18on:* → 1.79
 *
 * Mitigated Vulnerabilities:
 * 1. Google Protobuf
 *    - CVE-2024-7254: Potential security vulnerability
 *
 * 2. Netty Components
 *    - CVE-2025-25193: Denial of Service Vulnerability
 *    - CVE-2024-47535: Network security vulnerability
 *    - CVE-2024-29025: Remote code execution risk
 *    - CVE-2023-4586: Information disclosure vulnerability
 *    - CVE-2023-34462: Potential denial of service
 *    - CVE-2025-58056: Inconsistent Interpretation of HTTP Requests
 *    - CVE-2025-58057: mproper Handling of Highly Compressed Data
 *
 * 3. Bouncy Castle
 *    - CVE-2024-34447: Cryptographic security issue
 *    - CVE-2024-30172: Potential encryption vulnerability
 *    - CVE-2024-30171: Security protocol weakness
 *    - CVE-2024-29857: Cryptographic implementation flaw
 */
configurations.all {
    resolutionStrategy.eachDependency {
        // Google Protobuf
        if (requested.group == "com.google.protobuf" && requested.name == "protobuf-java") {
            useVersion("3.25.6")
            because("CVE-2024-7254")
        }

        // Netty Components
        if (requested.group == "io.netty" &&
            listOf(
                "netty-buffer",
                "netty-codec-http2",
                "netty-handler-proxy",
            ).contains(requested.name)
        ) {
            useVersion("4.1.127.Final")
            because("CVE-2025-25193, CVE-2024-47535, CVE-2024-29025, CVE-2023-4586, CVE-2023-34462, CVE-2025-55163, CVE-2025-58056, CVE-2025-58057")
        }

        if (requested.group == "org.apache.commons" && requested.name == "commons-lang3") {
            useVersion(libs.versions.commons.lang.get())
            because("CVE-2025-48924")
        }

        checkAndOverrideBouncyCastleVersion(project)
    }
}

tasks {
    named<ProcessResources>("processCommonResources") {
        val propertiesToExpand = mapOf(
            "projectVersion" to project.version,
            "kotlinNativeVersion" to project.kotlinNativeVersion,
            "kotlinWebNpmToolingDirName" to kotlinWebNpmToolingDirName,
            "bouncyCastleVersion" to libs.versions.bouncycastle.get(),
        )
        for ((name, value) in propertiesToExpand) {
            inputs.property(name, value)
        }
        filesMatching("project.properties") {
            expand(propertiesToExpand)
        }
    }

    withType<ShadowJar>().configureEach {
        relocate("com.github.gundy", "$kotlinEmbeddableRootPackage.com.github.gundy")
        val baseSourcePackage = "org.jetbrains.kotlin"
        val baseTargetPackage = "org.jetbrains.kotlin.gradle.internal"
        relocate("kotlinx.serialization", baseTargetPackage)
        val packages: Map<String, List<String>> = mapOf(
            "analyzer" to emptyList(),
            "build" to listOf(
                "org.jetbrains.kotlin.build.report.**",
            ),
            "backend" to emptyList(),
            "builtins" to emptyList(),
            "config" to listOf(
                "org.jetbrains.kotlin.config.ApiVersion**", // used a lot in buildscripts
                "org.jetbrains.kotlin.config.JvmTarget**", // used a lot in buildscripts
                "org.jetbrains.kotlin.config.KotlinCompilerVersion", // used a lot in buildscripts
                "org.jetbrains.kotlin.config.Services**", // required to initialize `CompilerEnvironment`
            ),
            "constant" to emptyList(),
            "container" to emptyList(),
            "contracts" to emptyList(),
            "descriptors" to emptyList(),
            "extensions" to emptyList(),
            "idea" to emptyList(),
            "ir" to emptyList(),
            "kapt3.diagnostic" to emptyList(),
            "load" to emptyList(),
            "metadata" to emptyList(),
            "modules" to emptyList(),
            "mpp" to emptyList(),
            "name" to emptyList(),
            "platform" to emptyList(),
            "progress" to emptyList(),
            "renderer" to emptyList(),
            "resolve" to emptyList(),
            "serialization" to emptyList(),
            "storage" to emptyList(),
            "types" to emptyList(),
            "type" to emptyList(),
            "utils" to emptyList(),
            "util" to listOf(
                "org.jetbrains.kotlin.util.Logger", // symbol from a standalone published artifact, don't relocate usages
                "org.jetbrains.kotlin.util.UtilKt", // class from kotlin-util-io which is a transitive API dependency of KGP-API, don't relocate usages
                "org.jetbrains.kotlin.util.capitalizeDecapitalize.CapitalizeDecapitalizeKt", // used in standalone published artifacts that the plugin depends on
            ),
        )
        packages.forEach { (pkg, exclusions) ->
            relocate("$baseSourcePackage.$pkg.", "$baseTargetPackage.$pkg.") {
                exclusions.forEach { exclude(it) }
            }
        }

        /*
        Disable Kotlin Module remapping to allow our own 'KotlinModuleMetadataVersionBasedSkippingTransformer' to run
         */
        enableKotlinModuleRemapping = false
        transform(KotlinModuleMetadataVersionBasedSkippingTransformer::class.java) {
            /*
             * This excludes .kotlin_module files for compiler modules from the fat jars.
             * These files are required only at compilation time, but we include the modules only for runtime
             * Hack for not limiting LV to 1.8 for those modules. To be removed after KT-70247
             */
            pivotVersion = KotlinMetadataPivotVersion(1, 9, 0)
        }
        asmDeprecation {
            val exclusions = listOf(
                "org.jetbrains.kotlin.gradle.**", // part of the plugin
                "org.jetbrains.kotlin.statistics.**", // part of the plugin
                "org.jetbrains.kotlin.tooling.**", // part of the plugin
                "org.jetbrains.kotlin.org.**", // already shadowed dependencies
                "org.jetbrains.kotlin.com.**", // already shadowed dependencies
                "org.jetbrains.kotlin.it.unimi.**", // already shadowed dependencies
                "org.jetbrains.kotlin.internal.**", // already internal package
            )
            val deprecationMessage = """
                You're using a Kotlin compiler class bundled into KGP for its internal needs.
                This is discouraged and will not be supported in future releases.
                The class in this artifact is scheduled for removal in Kotlin 2.2. Please define dependency on it in an alternative way.
                See https://kotl.in/gradle/internal-compiler-symbols for more details
            """.trimIndent()
            deprecateClassesByPattern("org.jetbrains.kotlin.**", deprecationMessage, exclusions)
        }
    }
}

gradlePlugin {
    plugins {
        create("kotlinJvmPlugin") {
            id = "org.jetbrains.kotlin.jvm"
            description = "Kotlin JVM plugin"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
        create("kotlinJsPlugin") {
            id = "org.jetbrains.kotlin.js"
            description = "Kotlin JS plugin"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.plugin.KotlinJsPluginWrapper"
        }
        create("kotlinMultiplatformPlugin") {
            id = "org.jetbrains.kotlin.multiplatform"
            description = "Kotlin Multiplatform plugin"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
        create("kotlinAndroidPlugin") {
            id = "org.jetbrains.kotlin.android"
            description = "Kotlin Android plugin"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
        create("kotlinParcelizePlugin") {
            id = "org.jetbrains.kotlin.plugin.parcelize"
            description = "Kotlin Parcelize plugin"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.internal.ParcelizeSubplugin"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
        create("kotlinKaptPlugin") {
            id = "org.jetbrains.kotlin.kapt"
            description = "Kotlin Kapt plugin"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
        create("kotlinScriptingPlugin") {
            id = "org.jetbrains.kotlin.plugin.scripting"
            description = "Gradle plugin for kotlin scripting"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.scripting.internal.ScriptingGradleSubplugin"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
        create("kotlinNativeCocoapodsPlugin") {
            id = "org.jetbrains.kotlin.native.cocoapods"
            description = "Kotlin Native plugin for CocoaPods integration"
            displayName = description
            implementationClass = "org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
    }
}

// Workaround for KT-75550
tasks.named("gradle813Jar") {
    enabled = false
}

tasks.withType<Jar>().configureEach {
    if (name.endsWith("SourcesJar")) {
        // FIXME: Entry org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt is a duplicate
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

kotlin {
    target.compilations.getByName("common").configurations.pluginConfiguration.dependencies.add(
        dependencies.create("org.jetbrains.kotlin:kotlin-serialization-compiler-plugin-embeddable:${libs.versions.kotlin.`for`.gradle.plugins.compilation.get()}")
    )
}

val generateKgpBuildConstants = registerGenerateKgpBuildConstantsTask {
    defaultYarnVersion = libs.versions.yarn
}

kotlin.sourceSets.common {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    generatedKotlin.srcDir(generateKgpBuildConstants)
}


// com.gradle.plugin-publish unconditionally adds a javadocJar to the publication
// (in afterEvaluate); drop the javadoc variant from the published component.
afterEvaluate {
    (components["java"] as org.gradle.api.component.AdhocComponentWithVariants)
        .withVariantsFromConfiguration(configurations["javadocElements"]) { skip() }
}
