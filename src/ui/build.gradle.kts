// Shared Compose screens and view models. Talks to the domain use cases only.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)

    androidLibrary {
        namespace = "io.github.fugeekies.timeflies.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
        }
    }
}
