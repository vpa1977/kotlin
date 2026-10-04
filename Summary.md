# Gradle Build Analysis Summary

## Project Overview

This is the **Kotlin programming language** repository - a large monorepo containing the Kotlin compiler, standard library, Gradle plugins, and related tooling. The build uses **Gradle 9.4.1** with Kotlin DSL and configuration cache enabled.

- **Root Project**: `kotlin`
- **Gradle Version**: 9.4.1
- **Kotlin DSL Version**: 2.3.0 (embedded)
- **Bootstrap Kotlin Version**: 2.4.0-dev-8449
- **Default Snapshot Version**: 2.4.255-SNAPSHOT
- **Java Toolchain**: Multiple JDKs (1.8, 11, 17, 21)

## Project Structure

The build contains **400+ subprojects** organized into these main areas:

| Area | Path | Description |
|------|------|-------------|
| Compiler | `compiler/` | Kotlin compiler (frontend, FIR, IR, backends) |
| Analysis API | `analysis/` | Code analysis and IDE support |
| Standard Library | `libraries/stdlib/` | kotlin-stdlib, kotlin-test, kotlin-reflect |
| Gradle Plugins | `libraries/tools/kotlin-gradle-plugin/` | KGP and related plugins |
| Compiler Plugins | `plugins/` | allopen, noarg, serialization, compose, etc. |
| Native | `kotlin-native/`, `native/` | Kotlin/Native compiler and runtime |
| JS/Wasm | `js/`, `wasm/` | JavaScript and WebAssembly backends |
| Build Tools | `compiler/build-tools/` | Build tools API and implementations |

## Dependencies

### Root Project (build.gradle.kts)

The root project applies security fixes and global configurations but has no direct dependencies.

### Key Subproject Dependencies

#### :kotlin-stdlib (Kotlin Standard Library)

| Scope | Dependencies |
|-------|-------------|
| `jvmCompileClasspath` | `org.jetbrains:annotations:13.0` |
| `jvmRuntimeClasspath` | `org.jetbrains:annotations:13.0` |
| `commonTestApi` | `project :kotlin-test` |

#### :kotlin-compiler (Compiler Distribution)

| Scope | Dependencies |
|-------|-------------|
| `api` | `project :kotlin-stdlib-jdk8`, `project :kotlin-script-runtime`, `org.jetbrains.kotlin:kotlin-reflect:1.6.10`, `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0`, `project :compiler:build-tools:kotlin-build-tools-api` |
| `fatJarContents` | `javax.inject`, `org.jline:jline`, `org.fusesource.jansi:jansi`, `protobuf`, `com.google.code.findbugs:jsr305`, `io.vavr:vavr`, `kotlinx-collections-immutable-jvm`, `intellijCore`, `jna`, `jna-platform`, `intellij-fastutil`, `lz4-java`, `intellij-asm`, `guava`, `guava-failureaccess`, `aalto-xml`, `stax2-api`, `oro`, `intellij-jdom`, `log4j`, `streamex`, `antlr-runtime` |
| `libraries` | `kotlin-stdlib-jdk8`, `kotlin-stdlib` (JS/Wasm variants), `kotlinx-coroutines-core` |
| `compilerPlugins` | `kotlin-allopen-compiler-plugin`, `parcelize-compiler`, `kotlin-noarg-compiler-plugin`, `kotlin-power-assert-compiler-plugin`, `kotlin-sam-with-receiver-compiler-plugin`, `kotlinx-serialization-compiler-plugin`, `kotlin-lombok-compiler-plugin`, `kotlin-assignment-compiler-plugin`, `kotlin-scripting-compiler`, `compose-compiler-plugin` |

#### :kotlin-compiler-embeddable

| Scope | Dependencies |
|-------|-------------|
| `api` | `project :compiler:build-tools:kotlin-build-tools-api` |
| `runtimeOnly` | `kotlin-stdlib`, `project :kotlin-script-runtime`, `org.jetbrains.kotlin:kotlin-reflect:1.6.10`, `project :kotlin-daemon-embeddable`, `kotlinx-coroutines-core` |

#### :kotlin-gradle-plugin (KGP)

| Scope | Dependencies |
|-------|-------------|
| `commonApi` | `project :kotlin-gradle-plugins-bom`, `project :kotlin-gradle-plugin-api`, `project :libraries:tools:gradle:fus-statistics-gradle-plugin` |
| `commonImplementation` | `project :kotlin-gradle-plugin-idea`, `project :kotlin-gradle-plugin-idea-proto`, `project :native:kotlin-klib-commonizer-api`, `project :compiler:build-tools:kotlin-build-statistics`, `project :kotlin-util-klib-metadata` |
| `commonCompileOnly` | `kotlin-stdlib:1.7.0`, `compiler:cli`, `compiler:cli-base`, `compiler:arguments.common`, `compiler:compiler.version`, `compiler:config`, `compiler:config.jvm`, `compiler:ir.serialization.common`, `compiler:util`, `core:compiler.common`, `core:compiler.common.jvm`, `core:descriptors`, `core:util.runtime`, `core:language.model`, `core:language.targets`, `core:language.targets.jvm`, `core:language.version-settings`, `core:names`, `kotlin-build-common`, `js:js.config`, `wasm:wasm.config`, `guava:33.3.1-jre`, `daemon-common`, `kotlin-daemon-client`, `kotlin-gradle-compiler-types`, `kotlin-compiler-runner-unshaded`, `kotlin-gradle-statistics`, `kotlin-gradle-build-metrics`, `kotlin-build-tools-jdk-utils`, `android-gradle-plugin:8.13.2`, `android-tools:31.13.2`, `serviceMessages:2019.1.4`, `gson:2.11.0`, `kotlinx-serialization-json:1.4.0`, `semver4j:0.16.4`, `kotlin-tooling-metadata`, `kotlin-build-statistics`, `swift-export-standalone`, `asm-all:9.6.1`, `commons-compress:1.27.1`, `bcpkix-jdk18on:1.84`, `bcpg-jdk18on:1.84` |
| `commonRuntimeOnly` | `project :kotlin-compiler-runner` |
| `embedded` | `compiler:cli`, `compiler:cli-base`, `compiler:arguments.common`, `compiler:compiler.version`, `compiler:config`, `compiler:config.jvm`, `compiler:ir.serialization.common`, `compiler:util`, `core:compiler.common`, `core:compiler.common.jvm`, `core:descriptors`, `core:util.runtime`, `core:language.model`, `core:language.targets`, `core:language.targets.jvm`, `core:language.version-settings`, `core:names`, `kotlin-build-common`, `js:js.config`, `wasm:wasm.config`, `kotlin-gradle-build-metrics`, `kotlin-gradle-statistics`, `asm-all:9.6.1`, `gson:2.11.0`, `kotlinx-serialization-json:1.4.0`, `guava:33.3.1-jre`, `guava-failureaccess:1.0.2`, `serviceMessages:2019.1.4`, `kotlin-tooling-metadata`, `semver4j:0.16.4`, `commons-compress:1.27.1` |
| `testCompileClasspath` | All compileClasspath deps plus: `project :compiler`, `project :kotlin-build-common`, `project :compiler:test-infrastructure-utils`, `project :kotlin-compiler-runner`, `kotlin-test-junit5:2.2.21`, `junit-jupiter-api:5.12.2`, `junit-jupiter-params:5.12.2`, `lincheck:2.28.1`, `kotlin-reflect:1.9.21`, `slf4j-api:2.0.17` |

### Version Catalog (gradle/libs.versions.toml)

Key dependency versions:

| Library | Version |
|---------|---------|
| Kotlin Release | 2.3.20-RC |
| Kotlin for Gradle Plugins Compilation | 2.2.21 |
| Android Gradle Plugin | 8.13.2 |
| Android Tools | 31.13.2 |
| Guava | 33.3.1-jre |
| Gson | 2.11.0 |
| JUnit 4 | 4.13.2 |
| JUnit 5 | 5.12.2 |
| kotlinx-coroutines | 1.8.0 |
| kotlinx-serialization | 1.7.3 |
| Protobuf | 4.28.2 |
| Bouncy Castle | 1.84 |
| Commons Compress | 1.27.1 |
| Commons IO | 2.18.0 |
| Commons Lang | 3.18.0 |
| Dokka | 2.2.0 |
| Shadow | 9.3.2 |
| Lincheck | 2.28.1 |
| Ktor | 3.1.3 |
| Jackson | 2.21.1 |
| ASM (IntelliJ) | 9.6.1 |

## Plugins

### Root Project Plugins

| Plugin | Purpose |
|--------|---------|
| `base` | Base Gradle functionality |
| `idea` | IntelliJ IDEA integration |
| `org.jetbrains.gradle.plugin.idea-ext` (1.0.1) | Extended IDEA configuration |
| `build-time-report` | Build time reporting (convention plugin) |
| `java-instrumentation` | Java instrumentation support (convention plugin) |
| `modularized-test-configurations` | Modularized test setup (convention plugin) |
| `resolve-dependencies` | Dependency resolution utilities (convention plugin) |
| `org.gradle.crypto.checksum` (1.4.0) | Checksum generation for artifacts |
| `signing-convention` | Artifact signing (convention plugin) |
| `org.jetbrains.kotlin.jvm` (apply false) | Kotlin JVM plugin (available to subprojects) |
| `org.jetbrains.kotlin.plugin.serialization` (apply false) | Kotlin serialization plugin |
| `kotlin.native.build-tools-conventions` (apply false) | Native build tools (when enabled) |
| `jvm-toolchains` | JVM toolchain support |
| `org.jetbrains.kotlinx.binary-compatibility-validator` (apply false) | Binary compatibility validation |
| `gradle.node` (apply false) | Node.js integration |
| `nodejs-cache-redirector-configuration` | Node.js cache redirector (convention plugin) |
| `gradle-plugins-documentation` (apply false) | Plugin documentation (convention plugin) |
| `com.autonomousapps.dependency-analysis` (3.6.1) | Dependency analysis |
| `project-tests-convention` (apply false) | Project test conventions |
| `test-data-manager-root` | Test data management (convention plugin) |

### Settings Plugins (settings.gradle.kts)

| Plugin | Purpose |
|--------|---------|
| `kotlin-bootstrap` | Kotlin bootstrap configuration (convention plugin) |
| `jvm-toolchain-provisioning` | JVM toolchain provisioning (convention plugin) |
| `kotlin-daemon-config` | Kotlin daemon configuration (convention plugin) |
| `cache-redirector` | Cache redirector (convention plugin) |
| `de.undercouch.download` (5.1.0) | Download task support |

### Convention Plugins (repo/gradle-build-conventions/)

These are included build convention plugins applied to subprojects:

| Plugin ID | Source | Applied To | Purpose |
|-----------|--------|------------|---------|
| `common-configuration` | buildsrc-compat | All projects (via allprojects) | Common project configuration |
| `project-tests-convention` | project-tests-convention | 133 subprojects | Test task configuration |
| `test-inputs-check` | project-tests-convention | 63 subprojects | Test input validation |
| `java-test-fixtures` | Gradle core | 82 subprojects | Java test fixtures support |
| `generated-sources` | generators | 21 subprojects | Generated source sets |
| `d8-configuration` | d8-configuration | 16 subprojects | D8 dexer configuration |
| `binaryen-configuration` | binaryen-configuration | 6 subprojects | Binaryen toolchain for Wasm |
| `nodejs-configuration` | nodejs-configuration | 5 subprojects | Node.js configuration |
| `nodejs-cache-redirector-configuration` | buildsrc-compat | 8 subprojects | Node.js cache redirector |
| `android-sdk-provisioner` | android-sdk-provisioner | 5 subprojects | Android SDK provisioning |
| `gradle-plugin-common-configuration` | gradle-plugins-common | 17 subprojects | Gradle plugin common config |
| `gradle-plugin-compiler-dependency-configuration` | gradle-plugins-common | 30 subprojects | Compiler dependency config for Gradle plugins |
| `gradle-plugin-published-compiler-dependency-configuration` | gradle-plugins-common | 7 subprojects | Published compiler deps |
| `gradle-plugin-api-reference` | gradle-plugins-documentation | 3 subprojects | API reference documentation |
| `gradle-plugins-documentation` | gradle-plugins-documentation | 2 subprojects | Plugin documentation |
| `signing-convention` | buildsrc-compat | 4 subprojects | Artifact signing |
| `test-data-manager` | test-data-manager-convention | 8 subprojects | Test data management |
| `test-data-manager-root` | test-data-manager-convention | Root project | Test data management root |
| `kotlin-git.gradle-build-conventions.foreign-class-usage-checker` | foreign-class-usage-checker | 9 subprojects | Foreign class usage checking |
| `kotlin-git.gradle-build-conventions.binary-compatibility-extended` | binary-compatibility-extended | KGP | Extended binary compatibility |
| `asm-deprecating-transformer` | asm-deprecating-transformer | KGP | ASM deprecation transformer |
| `build-time-report` | buildsrc-compat | Root project | Build time reporting |
| `java-instrumentation` | buildsrc-compat | Root project | Java instrumentation |
| `modularized-test-configurations` | buildsrc-compat | Root project | Modularized tests |
| `resolve-dependencies` | buildsrc-compat | Root project | Dependency resolution |
| `kotlin.native.build-tools-conventions` | kotlin-native | 3 subprojects | Native build tools |
| `objc-export-header-generator-test-library` | buildsrc-compat | 5 subprojects | ObjC export header tests |
| `share-foreign-java-nullability-annotations` | buildsrc-compat | 2 subprojects | Nullability annotations sharing |
| `share-kotlin-wasm-custom-formatters` | buildsrc-compat | 2 subprojects | Wasm custom formatters |
| `test-symlink-transformation` | buildsrc-compat | 2 subprojects | Test symlink transformation |
| `native-dependencies` | kotlin-native | 6 subprojects | Native dependencies |
| `native-interop-plugin` | kotlin-native | 4 subprojects | Native interop |
| `benchmarking` | kotlin-native | 7 subprojects | Benchmarking support |
| `swift-benchmarking` | kotlin-native | 1 subproject | Swift benchmarking |
| `compile-to-bitcode` | kotlin-native | 1 subproject | Bitcode compilation |
| `platform-manager` | kotlin-native | 1 subproject | Platform management |
| `custom-kotlin-native-home` | kotlin-native | 2 subprojects | Custom Native home |
| `native-dependencies-downloader` | kotlin-native | 1 subproject | Native deps downloader |

### Kotlin Plugins Applied to Subprojects

| Plugin | Count | Purpose |
|--------|-------|---------|
| `kotlin("jvm")` | 458 | Kotlin JVM compilation |
| `kotlin("multiplatform")` | 365 | Kotlin Multiplatform |
| `org.jetbrains.kotlin.multiplatform` | 77 | Kotlin MPP (by ID) |
| `org.jetbrains.kotlin.jvm` | 49 | Kotlin JVM (by ID) |
| `kotlin("plugin.serialization")` | 16 | Kotlinx Serialization compiler plugin |
| `kotlin("test-common")` | 15 | kotlin-test common |
| `kotlin("test")` | 15 | kotlin-test |
| `kotlin("test-annotations-common")` | 14 | kotlin-test annotations |
| `kotlin("stdlib-common")` | 11 | kotlin-stdlib common |
| `kotlin("test-junit")` | 8 | kotlin-test JUnit |
| `kotlin("stdlib-js")` | 7 | kotlin-stdlib JS |
| `kotlin("stdlib")` | 7 | kotlin-stdlib |
| `kotlin("test-js")` | 5 | kotlin-test JS |
| `kotlin("android")` | 4 | Kotlin Android |
| `org.jetbrains.kotlin.android` | 3 | Kotlin Android (by ID) |
| `kotlin("plugin.power-assert")` | 2 | Power-assert compiler plugin |
| `kotlin("kapt")` | 1 | Kotlin annotation processing |
| `kotlin("plugin.compose")` | 1 | Compose compiler plugin |
| `kotlin("native.cocoapods")` | 1 | Native CocoaPods |
| `org.jetbrains.kotlin.plugin.allopen` | 1 | All-open compiler plugin |
| `org.jetbrains.kotlin.plugin.noarg` | 1 | No-arg compiler plugin |
| `org.jetbrains.kotlin.plugin.parcelize` | 1 | Parcelize compiler plugin |
| `org.jetbrains.kotlin.plugin.compose` | 1 | Compose compiler plugin (by ID) |
| `org.jetbrains.kotlin.kapt` | 5+ | Kapt (Groovy DSL) |

### Third-Party Plugins

| Plugin | Version | Applied To | Purpose |
|--------|---------|------------|---------|
| `com.android.library` | - | 16 subprojects | Android library |
| `com.android.application` | - | 7 subprojects | Android application |
| `com.android.kotlin.multiplatform.library` | - | 6 subprojects | Android KMP library |
| `org.jetbrains.kotlinx.binary-compatibility-validator` | 0.17.0 | 10 subprojects | Binary compatibility validation |
| `org.jetbrains.kotlin.native.cocoapods` | - | 9 subprojects | Native CocoaPods |
| `maven-publish` | - | 13+ subprojects | Maven publishing |
| `org.jetbrains.dokka` | 2.2.0 | 2 subprojects | Documentation generation |
| `com.gradleup.shadow` | 9.3.2 | 1 subproject | Shadow JAR |
| `com.github.johnrengelman.shadow` | 4.0.3 | 1 subproject | Shadow JAR (legacy) |
| `com.google.protobuf` | 0.9.6 | 1 subproject | Protobuf |
| `org.jetbrains.compose` | 1.7.0 | 1 subproject | Compose Multiplatform |
| `org.jetbrains.kotlinx.benchmark` | 0.4.6-1 | 1 subproject | Benchmarking |
| `org.jetbrains.kotlin.jupyter.api` | 0.12.0-62 | 1 subproject | Jupyter integration |
| `com.google.dagger.hilt.android` | 2.59.1 | 1 subproject | Hilt DI |
| `com.autonomousapps.dependency-analysis` | 3.6.1 | Root project | Dependency analysis |
| `org.gradle.crypto.checksum` | 1.4.0 | Root project | Checksum generation |
| `org.jetbrains.gradle.plugin.idea-ext` | 1.0.1 | Root project | IDEA extensions |
| `com.github.node-gradle.node` | 7.0.2 | 9 subprojects (via alias) | Node.js integration |

## Gradle User Home

The build uses `foo` as the Gradle user home directory (`--gradle-user-home foo`). This directory contains pre-populated build artifacts:

- **Location**: `/home/vladimirp/git/gradle-upgrade/kotlin/foo/`
- **Size**: ~2.8 GB
- **Contents**:
  - `caches/9.4.1/` - Gradle 9.4.1 caches
  - `caches/modules-2/files-2.1/` - 153 cached dependency groups (including `org.jetbrains.kotlin`, `com.android.tools.build`, `com.google.guava`, `org.junit`, etc.)
  - `caches/build-cache-1/` - Build cache entries
  - `caches/jars-9/` - JAR caches
  - `daemon/` - Gradle daemon logs
  - `native/` - Native integration caches
  - `workers/` - Worker process caches
  - `kotlin-profile/` - Kotlin build profile data

The pre-populated cache allows running Gradle tasks (like `projects`, `dependencies`) without downloading dependencies from remote repositories.

## Security Overrides

The build enforces security fixes for vulnerable transitive dependencies:

| Library | Override Version | CVEs Addressed |
|---------|-----------------|----------------|
| `org.apache.commons:commons-compress` | 1.27.1 | CVE-2024-26308, CVE-2024-25710, CVE-2023-42503 |
| `commons-io:commons-io` | 2.18.0 | CVE-2024-26308, CVE-2023-42503 |
| `org.apache.commons:commons-lang3` | 3.18.0 | CVE-2025-48924 |
| `com.google.protobuf:protobuf-java` | 3.25.6 | CVE-2024-7254 |
| `io.netty:*` (buffer, codec-http2, handler-proxy) | 4.1.127.Final | CVE-2025-25193, CVE-2024-47535, CVE-2024-29025, CVE-2023-4586, CVE-2023-34462, CVE-2025-55163, CVE-2025-58056, CVE-2025-58057 |
| `org.bouncycastle:bcpkix-jdk18on` | 1.84 | CVE-2024-34447, CVE-2024-30172, CVE-2024-30171, CVE-2024-29857 |

## Build Artifact Contributions

This section highlights the plugins and dependency scopes that directly shape the **published build artifacts** (Maven publications, `dist/kotlinc` distribution, embeddable/shadow JARs, SBOMs, signed zips).

### Artifact-Producing Plugins

| Plugin | Version | Contribution to Artifacts |
|--------|---------|---------------------------|
| `maven-publish` | Gradle core | Enables Maven publications; applied to ~84 subprojects (13 by `id(...)`, 71 via backticks). Every published Kotlin artifact flows through it. |
| `kotlin-build-publishing` (`KotlinBuildPublishingPlugin`) | convention plugin (`gradle-plugins-common`) | Wraps `maven-publish`; creates the adhoc component, `publishedCompile`/`publishedRuntime` consumable configurations, and the `Main` Maven publication. Activated via the `publish()` helper in **63+ subprojects**. |
| `com.gradleup.shadow` (Shadow) | 9.3.2 | Builds fat/shadow JARs with package relocation. Powers `runtimeJarWithRelocation()`, `embeddableCompiler()`, and `rewriteDefaultJarDepsToShadedCompiler()` — used by `:kotlin-gradle-plugin`, `:kotlin-compiler-embeddable`, and **24+ `*.embeddable` projects** (allopen, noarg, kapt, serialization, parcelize, scripting, etc.). |
| `org.spdx.spdx-gradle-plugin` | 0.10.0-dev-15 | Generates SPDX SBOMs (`*.spdx.json`) attached to Maven publications and the compiler dist (`configureSbom()` in `gradle-plugins-common/sbom.kt`). |
| `org.gradle.crypto.checksum` | 1.4.0 | SHA-256 checksums for distribution zips (root `secureZipTask` wrapping `zipCompiler`). |
| `signing-convention` + `signing` | convention plugin / Gradle core | GPG signing of publications (`useGpgCmd()`) and of dist zips (`zipCompilerSign`); applied to 4+ subprojects and the root. |
| `java-test-fixtures` | Gradle core | Produces `testFixturesApi/RuntimeElements` variants consumed as artifacts by 82 subprojects. |
| `de.undercouch.download` | 5.1.0 | Downloads external binaries (Node.js, Wasm tools) that get repackaged into toolchain artifacts. |
| `gradle-plugin-publish` (`com.gradle.publish:plugin-publish-plugin`) | 2.1.1 | Publishes Gradle plugins (KGP, subplugins) to the Gradle Plugin Portal; wired via `gradle-plugin-common-configuration`. |
| `com.autonomousapps.dependency-analysis` | 3.6.1 | Validates dependency declarations of artifact-producing projects (root + all subprojects except `compiler:build-tools`). |

### Artifact-Building Helpers (convention code in `repo/gradle-build-conventions/`)

| Helper | Source | Effect |
|--------|--------|--------|
| `runtimeJar()` | `repoArtifacts.kt` | Replaces default `jar` with one that embeds the `embedded` configuration (`addEmbeddedRuntime`). Used by 17+ published modules. |
| `runtimeJarWithRelocation()` | `repoArtifacts.kt` | Shadow JAR + relocation for `jvm-abi-gen`, `compose/compiler`, `kotlinx-metadata-klib`, `abi-tools`. |
| `embeddableCompiler()` / `rewriteDefaultJarDepsToShadedCompiler()` | `embeddable.kt` | Shadow JAR relocating `com.google.*`, `org.apache.*`, `com.intellij`, `org.jline`, `io.vavr`, `org.antlr`, etc. under `org.jetbrains.kotlin.*`. Used by 24+ embeddable artifacts. |
| `sourcesJar()` / `javadocJar()` / `standardPublicJars()` | `repoArtifacts.kt` | Attaches `-sources.jar` and `-javadoc.jar` variants to publications. |
| `testsJar()` / `testsJarToBeUsedAlongWithFixtures()` | `repoArtifacts.kt` | Publishes `-tests.jar` artifacts (`tests-jar` configuration). |
| `setupPublicJar()` | `setupPublicJar.kt` | Stamps `Implementation-Vendor=JetBrains`, title, and build number into JAR manifests. |
| `publishJarsForIde()` / `publishTestJarsForIde()` | `repoArtifacts.kt` | Fat JARs for the IntelliJ Kotlin plugin (`prepare/ide-plugin-dependencies/*`, 40+ projects). |

### Dependency Scopes Feeding Artifacts

| Scope / Configuration | Defined In | Contribution |
|----------------------|-----------|--------------|
| `embedded` | Custom (per-project, e.g. `:kotlin-gradle-plugin`, `:plugins:parcelize:parcelize-compiler`, `:plugins:noarg`, `:plugins:assign-plugin`, `:plugins:power-assert`, `:plugins:kotlin-dataframe`, `:plugins:kapt:kapt-compiler`, all `*.embeddable` projects) | Contents are **unzipped into the runtime JAR** (`addEmbeddedRuntime`). E.g. KGP embeds gson, guava, kotlinx-serialization-json, semver4j, serviceMessages, asm-all, commons-compress, plus compiler runtime modules (`compiler:cli`, `core:*`, etc.). |
| `fatJarContents`, `fatJarContentsStripServices`, `fatJarContentsStripMetadata`, `fatJarContentsStripVersions` | `:kotlin-compiler` (`prepare/compiler`) | Modules and libraries merged into `kotlin-compiler.jar`: all compiler modules + `intellij-core`, `guava`, `intellij-asm`, `jline`, `jansi`, `protobuf`, `antlr-runtime`, `vavr`, `fastutil`, `lz4-java`, `jna`, `aalto-xml`, `stax2-api`, `oro`, `jdom`, `log4j`, `streamex`, `jsr305`, `javax.inject`, `kotlinx-collections-immutable-jvm`. |
| `builtinsMetadata` | `:kotlin-compiler` | `*.kotlin_builtins` files extracted from `kotlin-stdlib` and packed into the compiler JAR. |
| `libraries`, `librariesStripVersion`, `librariesKotlinTest`, `compilerPlugins`, `compilerPluginsCompat`, `sources`, `distMavenContents`, `distCommonContents`, `distStdlibMinimalForTests`, `buildNumber` | `:kotlin-compiler` | Feed the `distKotlinc`/`distCommon`/`distMaven` sync tasks producing `dist/kotlinc/lib/*.jar` (60 files), `dist/common`, `dist/maven` — the layout packed by the root `zipCompiler` task into `kotlin-compiler-<version>.zip`. |
| `publishedCompile`, `publishedRuntime` | `KotlinBuildPublishingPlugin` (all published projects) | Consumable variants mapped to Maven `compile`/`runtime` scopes; extend `apiElements`/`runtimeElements`. |
| `tests-jar`, `testsJarConfig` | `testsJar()` helpers (e.g. `:compiler`) | Test JAR artifacts published alongside main artifacts. |
| `jpsBootstrap` | Root project | Bootstrap classpath configuration for the JPS build. |
| `apiElements`, `runtimeElements`, `sourcesElements`, `javadocElements` | Gradle `java`/`java-library` (most subprojects) | Standard outgoing variants; `runtimeJar`/`sourcesJar`/`javadocJar` helpers rewire them to custom artifact tasks. |

### Notable Artifact Content Sources

- **`kotlin-compiler-embeddable`** (`prepare/compiler-embeddable`): `runtimeOnly` deps (`kotlin-stdlib`, `kotlin-script-runtime`, `kotlin-reflect:1.6.10`, `kotlin-daemon-embeddable`, `kotlinx-coroutines-core`) merged via `embeddableCompiler()` Shadow JAR with package relocation.
- **Kotlin Gradle Plugin** (`libraries/tools/kotlin-gradle-plugin`): the published JAR is a Shadow JAR relocating compiler modules into `org.jetbrains.kotlin.gradle.internal.*` (packages listed in its `build.gradle.kts`), with an ASM-based deprecation transformer (`asm-deprecating-transformer`) marking bundled compiler classes as deprecated.
- **Compiler plugins in `dist/kotlinc/lib`**: each is a fat JAR of its `embedded` modules — e.g. `parcelize-compiler.jar` embeds `parcelize.{common,k1,k2,backend,cli}`; `kotlin-serialization-compiler-plugin.jar` additionally ships a `distCompat` variant via `compilerPluginsCompat`.
- **SBOMs**: generated from `runtimeClasspath` (plus extra configurations for `:kotlin-compiler`: `libraries`, `compilerPlugins`, `fatJarContents*`) and attached to every non-internal publication.
- **IDE plugin artifacts**: `prepare/ide-plugin-dependencies/*` projects (40+) repackage compiler/analysis modules as fat JARs for the IntelliJ Kotlin plugin via `publishJarsForIde()`/`publishTestJarsForIde()`; aggregated by root tasks `publishIdeArtifacts` / `installIdeArtifacts`.

## Build Configuration

Key `gradle.properties` settings:

- `org.gradle.parallel=true` - Parallel project execution
- `org.gradle.caching=true` - Build cache enabled
- `org.gradle.configuration-cache=true` - Configuration cache enabled
- `kotlin.stdlib.default.dependency=false` - No default stdlib dependency
- `kotlin.native.enabled=false` - Kotlin/Native disabled by default
- `cacheRedirectorEnabled=true` - JetBrains cache redirector enabled
- `kotlinLanguageVersion=2.4` - Default Kotlin language version
