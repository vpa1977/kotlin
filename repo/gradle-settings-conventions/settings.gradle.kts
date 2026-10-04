pluginManagement {
    apply(from = "cache-redirector/src/main/kotlin/cache-redirector.settings.gradle.kts")
    apply(from = "kotlin-bootstrap/src/main/kotlin/kotlin-bootstrap.settings.gradle.kts")

    repositories {
        maven(url = "https://redirector.kotlinlang.org/maven/kotlin-dependencies")
        mavenCentral { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
        gradlePluginPortal()
    }
}

buildscript {
    val buildGradlePluginVersion = extra.get("kotlin.build.gradlePlugin.version")
    repositories {
        maven(url = "https://redirector.kotlinlang.org/maven/kotlin-dependencies")
        mavenCentral { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-build-gradle-plugin:$buildGradlePluginVersion")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

include(":jvm-toolchain-provisioning")
include(":kotlin-daemon-config")
include(":cache-redirector")
include(":kotlin-bootstrap")
