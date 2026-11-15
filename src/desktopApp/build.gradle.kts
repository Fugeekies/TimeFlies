// Windows app: composition root, window, and later the tray icon and notification.
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.ui)
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
}

compose.desktop {
    application {
        mainClass = "io.github.fugeekies.timeflies.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "TimeFlies"
            packageVersion = "1.0.0"
        }
    }
}

// `./gradlew :desktopApp:run -Dtimeflies.debug=true` hands the option to the app (debug panel, fake date).
// Only the local run task gets it: the installer never does (docs/DEVELOPMENT.md, "Panneau de débogage").
val debugOption = providers.systemProperty("timeflies.debug")
tasks.withType<JavaExec>().matching { it.name == "run" }.configureEach {
    debugOption.orNull?.let { systemProperty("timeflies.debug", it) }
}
