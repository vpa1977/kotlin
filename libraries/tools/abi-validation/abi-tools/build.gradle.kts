plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

publish()

runtimeJar()
sourcesJar()

dependencies {
    api(project(":libraries:tools:abi-validation:abi-tools-api"))
    api(kotlinStdlib())

    implementation(project(":kotlin-metadata-jvm"))
    implementation(project(":kotlin-klib-abi-reader"))

    compileOnly(libs.intellij.asm)
    embedded(libs.intellij.asm)

    implementation(libs.diff.utils)
}

runtimeJarWithRelocation {
    from(mainSourceSet.output)
    relocate("org.jetbrains.org.objectweb.asm", "org.jetbrains.kotlin.abi.tools.org.objectweb.asm")
}


// we create ABI dump only for `mainSourceSet.output` because in `libs.intellij.asm` is not a part of ABI, and we will exclude it in any way
