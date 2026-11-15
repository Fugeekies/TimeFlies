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
            windows {
                // Installer, shortcuts and executable: the winged hourglass (assets/icon/, docs/UI_DESIGN_SPEC.md).
                iconFile.set(rootProject.file("assets/icon/timeflies.ico"))
            }
        }
    }
}

// Window and taskbar icon, copied from assets/icon/ so the drawing has a single source.
tasks.processResources {
    from(rootProject.file("assets/icon/timeflies-256.png")) { into("icon") }
}

// `./gradlew :desktopApp:run '-Dtimeflies.debug=true'` hands the option to the app (debug panel, fake date);
// the quotes are needed in PowerShell, which otherwise splits the option at the dot.
// Only the local run task gets it: the installer never does (docs/DEVELOPMENT.md, "Panneau de débogage").
val debugOption = providers.systemProperty("timeflies.debug")
tasks.withType<JavaExec>().matching { it.name == "run" }.configureEach {
    debugOption.orNull?.let { systemProperty("timeflies.debug", it) }
}
