import org.gradle.plugin.compatibility.compatibility

plugins {
    id("gradle-plugin-common-configuration")
}

dependencies {
    commonApi(platform(project(":kotlin-gradle-plugins-bom")))
}

gradlePlugin {
    plugins {
        create("assignment") {
            id = "org.jetbrains.kotlin.plugin.assignment"
            displayName = "Kotlin Assignment compiler plugin"
            description = displayName
            implementationClass = "org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentSubplugin"

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
    }
}


// com.gradle.plugin-publish unconditionally adds a javadocJar to the publication
// (in afterEvaluate); drop the javadoc variant from the published component.
afterEvaluate {
    (components["java"] as org.gradle.api.component.AdhocComponentWithVariants)
        .withVariantsFromConfiguration(configurations["javadocElements"]) { skip() }
}
