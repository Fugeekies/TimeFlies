// Implementations of the domain ports (JSON events, DataStore settings, clock).
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "io.github.fugeekies.timeflies.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.datastore.core.okio)
            // The apps hand the file system and the settings file (okio types) to DataStoreSettingsRepository.
            api(libs.okio)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

// The JVM tests also validate the real event files shipped with the app (assets/events, docs/EVENTS_DATA.md).
val eventsDir = rootProject.layout.projectDirectory.dir("assets/events")
tasks.withType<Test>().configureEach {
    inputs.dir(eventsDir).withPathSensitivity(PathSensitivity.RELATIVE).optional()
    systemProperty("timeflies.eventsDir", eventsDir.asFile.absolutePath)
    testLogging { showStandardStreams = true }
}
