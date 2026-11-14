// Android app: composition root, and later the Glance widget and its daily worker.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "io.github.fugeekies.timeflies.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.fugeekies.timeflies"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.0-dev"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.ui)
    implementation(libs.androidx.activity.compose)
}
