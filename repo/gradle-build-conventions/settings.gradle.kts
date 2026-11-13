pluginManagement {
    includeBuild("../gradle-settings-conventions")

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("kotlin-bootstrap")
    id("develocity")
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
    repositories {
        intellijDependencies()
        googleAndroidRepository()
        mavenCentral()
        gradlePluginPortal()
    }
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

include(":buildsrc-compat")
include(":analysis-api-artifact")
include(":generators")
include(":project-tests-convention")
include(":android-sdk-provisioner")
include(":asm-deprecating-transformer")
include(":binary-compatibility-extended")
include(":gradle-plugins-documentation")
include(":gradle-plugins-common")
include(":kgp-npm-tooling-helper")
include(":d8-configuration")
// TODO: uncomment after bootstrap
// include(":swc-configuration")
include(":foreign-class-usage-checker")
include(":binaryen-configuration")
include(":nodejs-configuration")
include(":test-data-manager-convention")
include(":utilities")
include(":test-federation-convention")
include(":repo-test-fixtures")
include(":java-flight-recorder")
include(":test-inputs-check-v2")

