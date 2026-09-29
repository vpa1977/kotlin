plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core:compiler.common")) { isTransitive = false }
    api(project(":core:language.model")) { isTransitive = false }
    api(project(":core:language.targets")) { isTransitive = false }
    api(project(":compiler:config")) { isTransitive = false }
    api(project(":native:kotlin-native-utils"))
    compileOnly(intellijCore())

    compileOnly(project(":core:metadata")) { isTransitive = false }
    embedded(project(":core:metadata")) { isTransitive = false }
}

sourceSets {
    "main" {
        projectDefault()
        java.srcDir("gen")
    }
}

