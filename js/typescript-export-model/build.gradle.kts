plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core:compiler.common"))
    api(project(":js:js.serializer"))
}

sourceSets {
    "main" { projectDefault() }
}

kotlin {
    explicitApi()
}
