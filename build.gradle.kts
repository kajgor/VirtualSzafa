plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("multiplatform") version "2.0.21" apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
    id("io.ktor.plugin") version "2.3.12" apply false
}

allprojects {
    group = "pl.virtualszafa"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
