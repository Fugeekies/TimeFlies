// Business rules: entities, ports, use cases. Depends on nothing but kotlinx-datetime (added in milestone 2).
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    jvmToolchain(21)

    androidLibrary {
        namespace = "io.github.fugeekies.timeflies.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    jvm("desktop")
}
