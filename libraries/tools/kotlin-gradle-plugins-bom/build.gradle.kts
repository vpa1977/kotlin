import plugins.signLibraryPublication

plugins {
    id("java-platform")
    id("maven-publish")
}

dependencies {
    constraints {
        // kotlin-gradle-plugin-api
        api(project(":kotlin-gradle-plugin-api"))
        api(project(":kotlin-gradle-plugin-annotations"))
        api(project(":native:kotlin-native-utils"))
        api(project(":kotlin-tooling-core"))
        api(project(":libraries:tools:gradle:fus-statistics-gradle-plugin"))

        // plugins
        api(project(":kotlin-gradle-plugin"))
        api(project(":kotlin-sam-with-receiver"))
        api(project(":kotlin-assignment"))
    }
}

configureCommonPublicationSettingsForGradle(signLibraryPublication, sbom = false)

publishing {
    publications {
        create<MavenPublication>("myPlatform") {
            from(components["javaPlatform"])
            pom {
                packaging = "pom"
            }
        }
    }
}
