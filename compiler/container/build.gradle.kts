plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core:util.runtime"))
    api(commonDependency("javax.inject"))
    compileOnly(kotlinStdlib())
    compileOnly(intellijCore())
}

sourceSets {
    "main" { projectDefault() }
}
