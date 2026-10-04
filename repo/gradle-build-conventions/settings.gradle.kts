pluginManagement {
    includeBuild("../gradle-settings-conventions")

    repositories {
        maven(url = "https://redirector.kotlinlang.org/maven/kotlin-dependencies")
        mavenCentral { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
        google { setUrl("https://cache-redirector.jetbrains.com/dl.google.com/dl/android/maven2") }
        gradlePluginPortal()
    }
}

buildscript {
    val buildGradlePluginVersion = extra["kotlin.build.gradlePlugin.version"]
    repositories {
        maven(url = "https://redirector.kotlinlang.org/maven/kotlin-dependencies")
        mavenCentral { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
        google { setUrl("https://cache-redirector.jetbrains.com/dl.google.com/dl/android/maven2") }
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

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

include(":buildsrc-compat")
include(":prepare-deps")
include(":generators")
include(":project-tests-convention")
include(":android-sdk-provisioner")
include(":asm-deprecating-transformer")
include(":binary-compatibility-extended")
include(":gradle-plugins-common")
include(":d8-configuration")
// TODO: uncomment after bootstrap
// include(":swc-configuration")
include(":foreign-class-usage-checker")
include(":binaryen-configuration")
include(":test-data-manager-convention")
include(":utilities")
