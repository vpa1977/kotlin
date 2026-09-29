plugins {
    kotlin("jvm")
}

sourceSets {
    "main" { java.srcDirs("main") }
}

dependencies {
    api(kotlinStdlib("jdk8"))
    api(project(":core:util.runtime"))
    api(intellijPlatformUtil()) {
        exclude(module = "annotations")
    }
}
