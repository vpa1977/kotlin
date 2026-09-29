plugins {
    kotlin("jvm")
    id("gradle-plugin-compiler-dependency-configuration")
}

project.configureJvmToolchain(JdkMajorVersion.JDK_1_8)

dependencies {
    api(kotlinStdlib())
    api(project(":core:language.model"))
    implementation(project(":compiler:compiler.version"))
}

sourceSets {
    "main" { projectDefault() }
}
