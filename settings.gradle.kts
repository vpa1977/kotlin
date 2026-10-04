import java.io.File
import java.util.Properties

pluginManagement {
    includeBuild("repo/gradle-settings-conventions")
    includeBuild("repo/gradle-build-conventions")

    repositories {
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
            content {
                includeGroupByRegex("org\\.jetbrains\\.intellij\\.deps(\\..+)?")
                includeGroupByRegex("com.intellij.platform.*")
                includeGroupByRegex("org.jetbrains.jps.*")
            }
        }
        maven {
            url = uri("https://redirector.kotlinlang.org/maven/kotlin-dependencies")
            content {
                includeGroup("org.jetbrains.kotlin")
                includeGroup("org.spdx")
                includeGroup("org.jetbrains.kotlinx")
                includeGroup("org.jetbrains.kotlinx.benchmark")
            }
        }
        google {
            url = uri("https://cache-redirector.jetbrains.com/dl.google.com/dl/android/maven2")
            content {
                includeGroup("com.android.tools")
            }
        }
        mavenCentral {
            url = uri("https://cache-redirector.jetbrains.com/maven-central")
        }
        gradlePluginPortal()
    }

    plugins {
        id("de.undercouch.download") version "5.1.0"
    }
}

buildscript {
    val buildGradlePluginVersion = extra["kotlin.build.gradlePlugin.version"]
    repositories {
        maven(url = "https://redirector.kotlinlang.org/maven/kotlin-dependencies")
        mavenCentral { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-build-gradle-plugin:$buildGradlePluginVersion")
    }
}

plugins {
    id("kotlin-bootstrap")
    id("jvm-toolchain-provisioning")
    id("kotlin-daemon-config")
    id("cache-redirector")
}

val versionPropertiesFile = File(rootProject.projectDir, "gradle/versions.properties")
val versionProperties = Properties()
versionPropertiesFile.inputStream().use {
    versionProperties.load(it)
}
dependencyResolutionManagement {
    components {
        withModule("com.google.code.gson:gson") {
            allVariants {
                withDependencies {
                    add("com.google.code.gson:gson") {
                        version {
                            require(versionProperties["versions.gson"] as String)
                        }
                    }
                }
            }
        }

        withModule("org.apache.commons:commons-compress") {
            allVariants {
                withDependencies {
                    add("org.apache.commons:commons-compress") {
                        version {
                            require(versionProperties["versions.commons-compress"] as String)
                        }
                    }
                }
            }
        }

        withModule("commons-io:commons-io") {
            allVariants {
                withDependencies {
                    add("commons-io:commons-io") {
                        version {
                            require(versionProperties["versions.commons-io"] as String)
                        }
                    }
                }
            }
        }
    }

    versionCatalogs {
        create("composeRuntimeSnapshot") {
            from(files("plugins/compose/compose-runtime-snapshot-versions.toml"))
        }
    }
}

// modules: only the projects whose tasks are used by build.sh (kotlin step)
include(
    ":compiler",
    ":compiler:util",
    ":compiler:config",
    ":compiler:config.jvm",
    ":kotlin-util-io",
    ":kotlin-util-klib",
    ":kotlin-util-klib-metadata",
    ":kotlin-util-klib-abi",
    ":daemon-common",
    ":kotlin-daemon",
    ":kotlin-daemon-client",
    ":kotlin-preloader",
    ":compiler:arguments.common",
    ":compiler:arguments",
    ":compiler:container",
    ":compiler:resolution.common",
    ":compiler:resolution.common.jvm",
    ":compiler:resolution",
    ":compiler:serialization",
    ":compiler:psi:psi-api",
    ":compiler:psi:psi-impl",
    ":compiler:psi:psi-utils",
    ":compiler:psi:psi-frontend-utils",
    ":compiler:psi:parser",
    ":compiler:frontend",
    ":compiler:frontend.common",
    ":compiler:frontend.common-psi",
    ":compiler:frontend.java",
    ":compiler:frontend:cfg",
    ":kotlin-compiler-runner-unshaded",
    ":kotlin-compiler-runner",
    ":compiler:ir.tree",
    ":compiler:ir.tree:tree-generator",
    ":compiler:ir.psi2ir",
    ":compiler:ir.objcinterop",
    ":compiler:ir.backend.common",
    ":compiler:ir.backend.native",
    ":compiler:ir.actualization",
    ":compiler:ir.serialization.common",
    ":compiler:ir.serialization.jvm",
    ":compiler:ir.serialization.native",
    ":compiler:ir.interpreter",
    ":compiler:ir.inline",
    ":compiler:ir.validation",
    ":compiler:backend.jvm",
    ":compiler:backend.jvm.lower",
    ":compiler:backend.jvm.codegen",
    ":compiler:backend.jvm.entrypoint",
    ":compiler:backend",
    ":compiler:plugin-api",
    ":compiler:light-classes",
    ":compiler:javac-wrapper",
    ":compiler:cli:cli-arguments-generator",
    ":compiler:cli-base",
    ":compiler:cli",
    ":compiler:cli-jvm",
    ":compiler:cli-metadata",
    ":compiler:incremental-compilation-impl",
    ":native:kotlin-native-utils",
    ":native:frontend.native",
    ":native:kotlin-klib-commonizer-api",
    ":native:base",
    ":native:native.config",
    ":native:binary-options",
    ":native:analysis-api-based-export-common",
    ":core:names",
    ":core:language.model",
    ":core:language.targets",
    ":core:language.targets.jvm",
    ":core:language.version-settings",
    ":core:compiler.common",
    ":core:compiler.common.jvm",
    ":core:compiler.common.js",
    ":core:compiler.common.native",
    ":core:compiler.common.web",
    ":compiler:backend.common.jvm",
    ":core:descriptors",
    ":core:descriptors.jvm",
    ":core:deserialization",
    ":core:descriptors.runtime",
    ":core:metadata",
    ":core:metadata.jvm",
    ":core:deserialization.common",
    ":core:deserialization.common.jvm",
    ":compiler:frontend.common.jvm",
    ":core:util.runtime",
    ":compiler:compiler.version",
    ":dependencies:tools-jar-api",
    ":dependencies:intellij-core",
    ":kotlin-power-assert-compiler-plugin",
    ":kotlin-power-assert-compiler-plugin.common",
    ":kotlin-power-assert-compiler-plugin.frontend",
    ":kotlin-power-assert-compiler-plugin.backend",
    ":kotlin-power-assert-compiler-plugin.cli",
    ":js:js.config",
    ":wasm:wasm.config",
    ":kotlin-sam-with-receiver-compiler-plugin",
    ":kotlin-sam-with-receiver-compiler-plugin.embeddable",
    ":kotlin-sam-with-receiver-compiler-plugin.common",
    ":kotlin-sam-with-receiver-compiler-plugin.k1",
    ":kotlin-sam-with-receiver-compiler-plugin.k2",
    ":kotlin-sam-with-receiver-compiler-plugin.cli",
    ":kotlin-assignment-compiler-plugin",
    ":kotlin-assignment-compiler-plugin.common",
    ":kotlin-assignment-compiler-plugin.k1",
    ":kotlin-assignment-compiler-plugin.k2",
    ":kotlin-assignment-compiler-plugin.cli",
    ":kotlin-assignment-compiler-plugin.embeddable",
    ":gradle:generators:native-cache-kotlin-version",
    ":kotlin-script-runtime",
    ":kotlin-metadata",
    ":kotlin-metadata-jvm",
    ":prepare:build.version",
    ":kotlin-build-common",
    ":kotlin-compiler",
    ":kotlin-compiler-embeddable",
    ":kotlin-klib-abi-reader",
    ":kotlin-reflect",
    ":generators",
    ":generators:tree-generator-common",
    ":kotlin-build-tools-enum-compat",
    ":kotlin-gradle-compiler-types",
    ":kotlin-gradle-plugin-api",
    ":kotlin-gradle-plugin-annotations",
    ":kotlin-gradle-plugin-idea",
    ":kotlin-gradle-plugin-idea-proto",
    ":kotlin-gradle-statistics",
    ":kotlin-gradle-build-metrics",
    ":kotlin-gradle-plugin",
    ":kotlin-gradle-plugins-bom",
    ":compiler:build-tools:kotlin-build-statistics",
    ":libraries:tools:gradle:fus-statistics-gradle-plugin",
    ":kotlin-tooling-metadata",
    ":kotlin-tooling-core",
    ":kotlin-sam-with-receiver",
    ":kotlin-assignment",
    ":kotlin-daemon-embeddable",
    ":kotlin-annotations-jvm",
    ":kotlin-scripting-common",
    ":kotlin-scripting-jvm",
    ":kotlin-scripting-jvm-host-unshaded",
    ":kotlin-scripting-jvm-host",
    ":kotlin-scripting-compiler",
    ":kotlin-scripting-compiler-embeddable",
    ":kotlin-scripting-compiler-impl",
    ":kotlin-scripting-compiler-impl-embeddable",
    ":plugins:jvm-abi-gen",
    ":plugins:jvm-abi-gen-embeddable",
    ":libraries:tools:abi-validation:abi-tools",
    ":libraries:tools:abi-validation:abi-tools-api",
    ":libraries:tools:analysis-api-based-klib-reader",
    ":compiler:fir:cones",
    ":compiler:fir:tree",
    ":compiler:fir:tree:tree-generator",
    ":compiler:fir:raw-fir:raw-fir.common",
    ":compiler:fir:raw-fir:psi2fir",
    ":compiler:fir:raw-fir:light-tree2fir",
    ":compiler:fir:fir2ir",
    ":compiler:fir:fir2ir:jvm-backend",
    ":compiler:fir:providers",
    ":compiler:fir:semantics",
    ":compiler:fir:resolve",
    ":compiler:fir:plugin-utils",
    ":compiler:fir:fir-serialization",
    ":compiler:fir:fir-deserialization",
    ":compiler:fir:fir-jvm",
    ":compiler:fir:fir-native",
    ":compiler:fir:checkers",
    ":compiler:fir:checkers:checkers.jvm",
    ":compiler:fir:checkers:checkers.native",
    ":compiler:fir:checkers:checkers-component-generator",
    ":compiler:fir:diagnostic-renderers",
    ":compiler:fir:entrypoint",
    ":compiler:fir:analysis-tests",
    ":compiler:fir:analysis-tests:legacy-fir-tests",
    ":kotlinx-serialization-compiler-plugin",
    ":kotlinx-serialization-compiler-plugin.embeddable",
    ":kotlinx-serialization-compiler-plugin.common",
    ":kotlinx-serialization-compiler-plugin.k1",
    ":kotlinx-serialization-compiler-plugin.k2",
    ":kotlinx-serialization-compiler-plugin.backend",
    ":kotlinx-serialization-compiler-plugin.cli",
    ":compiler:build-tools:kotlin-build-tools-api",
    ":compiler:build-tools:kotlin-build-tools-impl",
    ":compiler:build-tools:kotlin-build-tools-compat",
    ":compiler:build-tools:kotlin-build-tools-jdk-utils",
    ":compiler:build-tools:kotlin-build-tools-options-generator",
    ":compiler:build-tools:util-kotlinpoet",
    ":compiler:build-tools:kotlin-build-tools-cri-impl",
    ":native:swift:sir",
    ":native:swift:sir:tree-generator",
    ":native:swift:sir-light-classes",
    ":native:swift:sir-printer",
    ":native:swift:sir-providers",
    ":native:swift:swift-export-standalone",
    ":analysis:low-level-api-fir",
    ":analysis:analysis-api-fir:analysis-api-fir-generator",
    ":analysis:analysis-api-fir",
    ":analysis:analysis-api",
    ":analysis:analysis-api-impl-base",
    ":analysis:analysis-api-platform-interface",
    ":analysis:analysis-internal-utils",
    ":analysis:kt-references",
    ":analysis:symbol-light-classes",
    ":analysis:light-classes-base",
    ":analysis:analysis-api-standalone",
    ":analysis:analysis-api-standalone:analysis-api-standalone-base",
    ":analysis:analysis-api-standalone:analysis-api-fir-standalone-base",
    ":analysis:decompiled:decompiler-to-psi",
    ":analysis:decompiled:decompiler-to-stubs",
    ":analysis:decompiled:decompiler-to-file-stubs",
    ":analysis:decompiled:decompiler-native",
    ":analysis:decompiled:light-classes-for-decompiled",
    ":kotlin-stdlib-common",
    ":kotlin-stdlib",
    ":kotlin-stdlib-jdk7",
    ":kotlin-stdlib-jdk8",
    ":compiler:backend.js",
    ":compiler:backend.wasm",
    ":compiler:cli-js",
    ":compiler:ir.serialization.js",
    ":core:compiler.common.wasm",
    ":compiler:fir:fir-js",
    ":compiler:fir:checkers:checkers.js",
    ":compiler:fir:checkers:checkers.wasm",
    ":compiler:fir:checkers:checkers.web.common",
    ":js:js.ast",
    ":js:js.frontend",
    ":js:js.frontend.common",
    ":js:js.parser",
    ":js:js.serializer",
    ":js:js.sourcemap",
    ":js:js.translator",
    ":js:typescript-export-model",
    ":js:typescript-printer",
    ":wasm:wasm.frontend",
    ":wasm:wasm.ir",
)

rootProject.name = "kotlin"

// Bootstrap shim projects: no tasks are used from them, but the kotlin-bootstrap
// settings convention references them for dependency substitution at configuration time.
include(
    ":dependencies:bootstrap:kotlin-stdlib-bootstrap",
    ":dependencies:bootstrap:kotlin-build-tools-api-bootstrap",
    ":dependencies:bootstrap:kotlin-build-tools-impl-bootstrap",
    ":dependencies:bootstrap:kotlin-build-tools-compat-bootstrap",
    ":dependencies:bootstrap:kotlin-build-tools-cri-impl-bootstrap",
    ":dependencies:bootstrap:kotlin-script-runtime-bootstrap",
    ":dependencies:bootstrap:kotlin-scripting-common-bootstrap",
    ":dependencies:bootstrap:kotlin-scripting-jvm-bootstrap",
    ":dependencies:bootstrap:kotlin-daemon-client-bootstrap",
    ":dependencies:bootstrap:kotlin-reflect-bootstrap",
    ":dependencies:bootstrap:kotlin-tooling-core-bootstrap",
    ":dependencies:bootstrap:kotlin-compiler-embeddable-bootstrap",
    ":dependencies:bootstrap:kotlin-compiler-runner-bootstrap"
)

project(":gradle:generators:native-cache-kotlin-version").projectDir = File("$rootDir/libraries/tools/gradle/generators/native-cache-kotlin-version")
project(":gradle:generators").projectDir = File("$rootDir/libraries/tools/gradle/generators")
project(":kotlin-stdlib-common").projectDir = File("$rootDir/libraries/stdlib/common")
project(":kotlin-stdlib").projectDir = File("$rootDir/libraries/stdlib")
project(":kotlin-stdlib-jdk7").projectDir = File("$rootDir/libraries/stdlib/jdk7")
project(":kotlin-stdlib-jdk8").projectDir = File("$rootDir/libraries/stdlib/jdk8")
project(":kotlin-script-runtime").projectDir = File("$rootDir/libraries/tools/script-runtime")
project(":kotlin-reflect").projectDir = File("$rootDir/libraries/reflect")
project(":kotlin-metadata").projectDir = File("$rootDir/libraries/kotlinx-metadata")
project(":kotlin-metadata-jvm").projectDir = File("$rootDir/libraries/kotlinx-metadata/jvm")
project(":kotlin-compiler").projectDir = File("$rootDir/prepare/compiler")
project(":kotlin-compiler-embeddable").projectDir = File("$rootDir/prepare/compiler-embeddable")
project(":kotlin-klib-abi-reader").projectDir = File("$rootDir/libraries/tools/klib-abi-reader")
project(":kotlin-preloader").projectDir = File("$rootDir/compiler/preloader")
project(":kotlin-build-common").projectDir = File("$rootDir/build-common")
project(":compiler:cli-base").projectDir = File("$rootDir/compiler/cli/cli-base")
project(":compiler:cli-jvm").projectDir = File("$rootDir/compiler/cli/cli-jvm")
project(":compiler:cli-metadata").projectDir = File("$rootDir/compiler/cli/cli-metadata")
project(":kotlin-daemon").projectDir = File("$rootDir/compiler/daemon")
project(":daemon-common").projectDir = File("$rootDir/compiler/daemon/daemon-common")
project(":kotlin-daemon-client").projectDir = File("$rootDir/compiler/daemon/daemon-client")
project(":kotlin-compiler-runner-unshaded").projectDir = File("$rootDir/compiler/compiler-runner-unshaded")
project(":kotlin-compiler-runner").projectDir = File("$rootDir/compiler/compiler-runner")
project(":compiler:ir.tree").projectDir = File("$rootDir/compiler/ir/ir.tree")
project(":compiler:ir.tree:tree-generator").projectDir = File("$rootDir/compiler/ir/ir.tree/tree-generator")
project(":compiler:ir.psi2ir").projectDir = File("$rootDir/compiler/ir/ir.psi2ir")
project(":compiler:ir.objcinterop").projectDir = File("$rootDir/compiler/ir/ir.objcinterop")
project(":compiler:ir.backend.common").projectDir = File("$rootDir/compiler/ir/backend.common")
project(":compiler:ir.backend.native").projectDir = File("$rootDir/compiler/ir/backend.native")
project(":compiler:ir.actualization").projectDir = File("$rootDir/compiler/ir/ir.actualization")
project(":compiler:ir.inline").projectDir = File("$rootDir/compiler/ir/ir.inline")
project(":compiler:ir.validation").projectDir = File("$rootDir/compiler/ir/ir.validation")
project(":compiler:backend.jvm").projectDir = File("$rootDir/compiler/ir/backend.jvm")
project(":core:compiler.common.js").projectDir = File("$rootDir/core/compiler.common.js")
project(":core:compiler.common.web").projectDir = File("$rootDir/core/compiler.common.web")
project(":compiler:backend.jvm.lower").projectDir = File("$rootDir/compiler/ir/backend.jvm/lower")
project(":compiler:backend.jvm.codegen").projectDir = File("$rootDir/compiler/ir/backend.jvm/codegen")
project(":compiler:backend.jvm.entrypoint").projectDir = File("$rootDir/compiler/ir/backend.jvm/entrypoint")
project(":compiler:ir.serialization.common").projectDir = File("$rootDir/compiler/ir/serialization.common")
project(":compiler:ir.serialization.jvm").projectDir = File("$rootDir/compiler/ir/serialization.jvm")
project(":compiler:ir.serialization.native").projectDir = File("$rootDir/compiler/ir/serialization.native")
project(":js:js.config").projectDir = File("$rootDir/js/js.config")
project(":wasm:wasm.config").projectDir = File("$rootDir/wasm/wasm.config")
project(":compiler:ir.interpreter").projectDir = File("$rootDir/compiler/ir/ir.interpreter")
project(":kotlin-util-io").projectDir = File("$rootDir/compiler/util-io")
project(":kotlin-util-klib").projectDir = File("$rootDir/compiler/util-klib")
project(":kotlin-util-klib-metadata").projectDir = File("$rootDir/compiler/util-klib-metadata")
project(":kotlin-util-klib-abi").projectDir = File("$rootDir/compiler/util-klib-abi")
project(":native:kotlin-native-utils").projectDir = File("$rootDir/native/utils")
project(":native:frontend.native").projectDir = File("$rootDir/native/frontend")
project(":native:kotlin-klib-commonizer-api").projectDir = File("$rootDir/native/commonizer-api")
project(":kotlin-power-assert-compiler-plugin").projectDir = File("$rootDir/plugins/power-assert/power-assert-compiler")
project(":kotlin-power-assert-compiler-plugin.common").projectDir = File("$rootDir/plugins/power-assert/power-assert-compiler/power-assert.common")
project(":kotlin-power-assert-compiler-plugin.frontend").projectDir = File("$rootDir/plugins/power-assert/power-assert-compiler/power-assert.frontend")
project(":kotlin-power-assert-compiler-plugin.backend").projectDir = File("$rootDir/plugins/power-assert/power-assert-compiler/power-assert.backend")
project(":kotlin-power-assert-compiler-plugin.cli").projectDir = File("$rootDir/plugins/power-assert/power-assert-compiler/power-assert.cli")
project(":kotlin-sam-with-receiver-compiler-plugin").projectDir = File("$rootDir/plugins/sam-with-receiver")
project(":kotlin-sam-with-receiver-compiler-plugin.embeddable").projectDir = File("$rootDir/plugins/sam-with-receiver/sam-with-receiver.embeddable")
project(":kotlin-sam-with-receiver-compiler-plugin.common").projectDir = File("$rootDir/plugins/sam-with-receiver/sam-with-receiver.common")
project(":kotlin-sam-with-receiver-compiler-plugin.k1").projectDir = File("$rootDir/plugins/sam-with-receiver/sam-with-receiver.k1")
project(":kotlin-sam-with-receiver-compiler-plugin.k2").projectDir = File("$rootDir/plugins/sam-with-receiver/sam-with-receiver.k2")
project(":kotlin-sam-with-receiver-compiler-plugin.cli").projectDir = File("$rootDir/plugins/sam-with-receiver/sam-with-receiver.cli")
project(":kotlin-assignment-compiler-plugin").projectDir = File("$rootDir/plugins/assign-plugin")
project(":kotlin-assignment-compiler-plugin.common").projectDir = File("$rootDir/plugins/assign-plugin/assign-plugin.common")
project(":kotlin-assignment-compiler-plugin.k1").projectDir = File("$rootDir/plugins/assign-plugin/assign-plugin.k1")
project(":kotlin-assignment-compiler-plugin.k2").projectDir = File("$rootDir/plugins/assign-plugin/assign-plugin.k2")
project(":kotlin-assignment-compiler-plugin.cli").projectDir = File("$rootDir/plugins/assign-plugin/assign-plugin.cli")
project(":kotlin-assignment-compiler-plugin.embeddable").projectDir = File("$rootDir/plugins/assign-plugin/assign-plugin.embeddable")
project(":kotlin-build-tools-enum-compat").projectDir = File("$rootDir/libraries/tools/kotlin-build-tools-enum-compat")
project(":kotlin-gradle-compiler-types").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-compiler-types")
project(":kotlin-gradle-plugin-api").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-plugin-api")
project(":kotlin-gradle-plugin-annotations").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-plugin-annotations")
project(":kotlin-gradle-plugin-idea").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-plugin-idea")
project(":kotlin-gradle-plugin-idea-proto").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-plugin-idea-proto")
project(":kotlin-gradle-statistics").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-statistics")
project(":kotlin-gradle-build-metrics").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-build-metrics")
project(":kotlin-gradle-plugin").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-plugin")
project(":kotlin-gradle-plugins-bom").projectDir = File("$rootDir/libraries/tools/kotlin-gradle-plugins-bom")
project(":kotlin-tooling-metadata").projectDir = File("$rootDir/libraries/tools/kotlin-tooling-metadata")
project(":kotlin-tooling-core").projectDir = File("$rootDir/libraries/tools/kotlin-tooling-core")
project(":kotlin-sam-with-receiver").projectDir = File("$rootDir/libraries/tools/kotlin-sam-with-receiver")
project(":kotlin-assignment").projectDir = File("$rootDir/libraries/tools/kotlin-assignment")
project(":kotlin-daemon-embeddable").projectDir = File("$rootDir/prepare/kotlin-daemon-embeddable")
project(":kotlin-annotations-jvm").projectDir = File("$rootDir/libraries/tools/kotlin-annotations-jvm")
project(":kotlin-scripting-common").projectDir = File("$rootDir/libraries/scripting/common")
project(":kotlin-scripting-jvm").projectDir = File("$rootDir/libraries/scripting/jvm")
project(":kotlin-scripting-jvm-host-unshaded").projectDir = File("$rootDir/libraries/scripting/jvm-host")
project(":kotlin-scripting-jvm-host").projectDir = File("$rootDir/libraries/scripting/jvm-host-embeddable")
project(":kotlin-scripting-compiler").projectDir = File("$rootDir/plugins/scripting/scripting-compiler")
project(":kotlin-scripting-compiler-embeddable").projectDir = File("$rootDir/plugins/scripting/scripting-compiler-embeddable")
project(":kotlin-scripting-compiler-impl").projectDir = File("$rootDir/plugins/scripting/scripting-compiler-impl")
project(":kotlin-scripting-compiler-impl-embeddable").projectDir = File("$rootDir/plugins/scripting/scripting-compiler-impl-embeddable")
project(":plugins:jvm-abi-gen").projectDir = File("$rootDir/plugins/jvm-abi-gen")
project(":plugins:jvm-abi-gen-embeddable").projectDir = File("$rootDir/plugins/jvm-abi-gen/embeddable")
project(":kotlinx-serialization-compiler-plugin").projectDir = File("$rootDir/plugins/kotlinx-serialization")
project(":kotlinx-serialization-compiler-plugin.embeddable").projectDir = File("$rootDir/plugins/kotlinx-serialization/kotlinx-serialization.embeddable")
project(":kotlinx-serialization-compiler-plugin.cli").projectDir = File("$rootDir/plugins/kotlinx-serialization/kotlinx-serialization.cli")
project(":kotlinx-serialization-compiler-plugin.backend").projectDir = File("$rootDir/plugins/kotlinx-serialization/kotlinx-serialization.backend")
project(":kotlinx-serialization-compiler-plugin.k1").projectDir = File("$rootDir/plugins/kotlinx-serialization/kotlinx-serialization.k1")
project(":kotlinx-serialization-compiler-plugin.k2").projectDir = File("$rootDir/plugins/kotlinx-serialization/kotlinx-serialization.k2")
project(":kotlinx-serialization-compiler-plugin.common").projectDir = File("$rootDir/plugins/kotlinx-serialization/kotlinx-serialization.common")
project(":compiler:cli-js").projectDir = File("$rootDir/compiler/cli/cli-js")
project(":compiler:backend.js").projectDir = File("$rootDir/compiler/ir/backend.js")
project(":compiler:backend.wasm").projectDir = File("$rootDir/compiler/ir/backend.wasm")
project(":compiler:ir.serialization.js").projectDir = File("$rootDir/compiler/ir/serialization.js")
