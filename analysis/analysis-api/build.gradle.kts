plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

dependencies {
    compileOnly(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }

    compileOnly(project(":core:language.model"))
    compileOnly(project(":core:language.targets"))
    compileOnly(project(":core:language.version-settings"))
    compileOnly(project(":compiler:psi:psi-api"))
    implementation(project(":compiler:backend"))
    compileOnly(project(":core:compiler.common"))
    compileOnly(project(":core:compiler.common.jvm"))
    compileOnly(project(":core:compiler.common.js"))
    implementation(project(":analysis:analysis-internal-utils"))
    implementation(project(":analysis:kt-references"))

    api(intellijCore())
    api(libs.intellij.asm)
    api(libs.guava)
}

sourceSets {
    "main" { projectDefault() }
}
