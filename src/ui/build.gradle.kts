// Shared Compose screens and view models. Talks to the domain use cases only.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "io.github.fugeekies.timeflies.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources.enable = true
    }
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(projects.domain)
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            implementation(libs.compose.material3.navigation.suite)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fugeekies.timeflies.ui.resources"
}
