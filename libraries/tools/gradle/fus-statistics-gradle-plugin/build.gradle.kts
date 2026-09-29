plugins {
    id("gradle-plugin-common-configuration")
}



tasks.named("publishPlugins") {
    enabled = false
}


// com.gradle.plugin-publish unconditionally adds a javadocJar to the publication
// (in afterEvaluate); drop the javadoc variant from the published component.
afterEvaluate {
    (components["java"] as org.gradle.api.component.AdhocComponentWithVariants)
        .withVariantsFromConfiguration(configurations["javadocElements"]) { skip() }
}
