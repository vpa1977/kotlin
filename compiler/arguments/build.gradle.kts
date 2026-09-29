plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

description = "Contains a unified representation of Kotlin compiler arguments for current and old Kotlin releases."

sourceSets {
    "main" {
        projectDefault()
    }
}

publish {
    artifactId = "kotlin-compiler-arguments-description"
}
runtimeJar()
sourcesJar()

dependencies {
    api(kotlinStdlib())

    implementation(libs.kotlinx.serialization.json)

    compileOnly(project(":compiler:arguments.common"))
    embedded(project(":compiler:arguments.common")) {
        isTransitive = false
    }
}

val generateJson = tasks.register<JavaExec>("generateJson") {
    classpath(sourceSets.named("main").flatMap { it.kotlin.classesDirectory })
    classpath(sourceSets.named("main").map { it.compileClasspath })
    mainClass.set("org.jetbrains.kotlin.arguments.serialization.json.JsonSerializerKt")

    val outputJsonInResources = sourceSets.named("main").map {
        // The first one is the default one
        it.resources.srcDirs.last().resolve("kotlin-compiler-arguments.json")
    }
    outputs.file(outputJsonInResources)
    argumentProviders.add {
        listOf(outputJsonInResources.get().path)
    }

}

tasks.named("processResources") {
    dependsOn(generateJson)
}
tasks.named("sourcesJar") {
    dependsOn(generateJson)
}
