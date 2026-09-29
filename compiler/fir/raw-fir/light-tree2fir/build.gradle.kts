plugins {
    kotlin("jvm")
}

group = "org.jetbrains.kotlin.fir"

repositories {
    mavenCentral()
    mavenLocal()
    maven { setUrl("https://www.jetbrains.com/intellij-repository/releases") }
    maven { setUrl("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies") }
}

dependencies {
    api(project(":compiler:fir:raw-fir:raw-fir.common"))
    implementation(project(":compiler:psi:psi-api"))
    implementation(project(":compiler:psi:psi-impl"))
    implementation(project(":compiler:psi:psi-frontend-utils"))
    implementation(project(":compiler:psi:parser"))
    implementation(kotlinxCollectionsImmutable())

    compileOnly(intellijCore())
    compileOnly(libs.guava)
}

sourceSets {
    "main" { projectDefault() }
}
