// Android app: composition root, the Glance widget and its daily worker.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

// Version of a release, given by release.yml (`-Ptimeflies.version=1.2.3`); none for a local build.
val releaseVersion: String? = providers.gradleProperty("timeflies.version").orNull

/** "1.2.3" → 10203 (minor and patch below 100). */
fun versionCodeOf(version: String): Int {
    val (major, minor, patch) = version.split('.').map(String::toInt)
    require(minor < 100 && patch < 100) { "Version $version: minor and patch must stay below 100" }
    return major * 10_000 + minor * 100 + patch
}

android {
    namespace = "io.github.fugeekies.timeflies.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.fugeekies.timeflies"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // 1.2.3 → 10203: always growing from one release to the next, so Android installs it over the old one.
        versionCode = releaseVersion?.let(::versionCodeOf) ?: 1
        versionName = releaseVersion ?: "0.0.0-dev"
        // Shown in Settings › À propos; null in a local build ("version de développement").
        buildConfigField("String", "RELEASE_VERSION", releaseVersion?.let { "\"$it\"" } ?: "null")
    }

    // The stable release key, from the repository secrets in release.yml (never in the code). Without it,
    // a local assembleRelease gives an unsigned APK.
    val keystore = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
    signingConfigs {
        if (keystore != null) {
            create("release") {
                storeFile = file(keystore)
                storePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").get()
                keyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").get()
                keyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").get()
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Required by the Compose Preview support of editors (VS Code Kotlin extension, Android Studio).
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

// Compose Preview also needs unit tests enabled on every variant, release included.
androidComponents {
    beforeVariants { variant ->
        variant.hostTests[com.android.build.api.variant.HostTestBuilder.UNIT_TEST_TYPE]?.enable = true
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
    // Home screen widget and its daily update (docs/UISpec/AndroidWidget.template.md).
    implementation(libs.glance.appwidget)
    implementation(libs.androidx.work.runtime)
}
