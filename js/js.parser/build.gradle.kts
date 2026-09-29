plugins {
    java
    kotlin("jvm")
}

dependencies {
    api(libs.antlr.runtime)

    api(kotlinStdlib())
    api(project(":js:js.ast"))
    compileOnly(intellijCore())
}

sourceSets {
    "main" { projectDefault() }
}
