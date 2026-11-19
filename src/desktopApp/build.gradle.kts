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

// Version of a release, given by release.yml (`-Ptimeflies.version=1.2.3`); none for a local build.
val releaseVersion: String? = providers.gradleProperty("timeflies.version").orNull

compose.desktop {
    application {
        mainClass = "io.github.fugeekies.timeflies.desktop.MainKt"
        // Shown in Settings › À propos ("TimeFlies v1.2.3"); absent from a local build.
        releaseVersion?.let { jvmArgs += "-Dtimeflies.version=$it" }
        nativeDistributions {
            // `createDistributable` only: an app folder (jpackage app-image, its own Java inside), packed by
            // Inno Setup (scripts/installer.iss) as for LMU-DeltaRacing. No .msi, so no WiX.
            targetFormats(TargetFormat.Exe)
            packageName = "TimeFlies"
            // jpackage wants numbers: a local build, without a release version, is 1.0.0 and says
            // "version de développement" in Settings since it gets no jpackage.app-version from a release.
            packageVersion = releaseVersion ?: "1.0.0"
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

// Screenshots of the presentation page (site/assets/), rendered off screen from the real screens:
// `./gradlew :desktopApp:siteScreenshots` (docs/DEVELOPMENT.md, "Page de présentation"). Kept out of the app and
// of the tests, so neither the installer nor the CI carries it.
val screenshots: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}
configurations[screenshots.implementationConfigurationName].extendsFrom(configurations.implementation.get())
configurations[screenshots.runtimeOnlyConfigurationName].extendsFrom(configurations.runtimeOnly.get())
dependencies {
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    screenshots.implementationConfigurationName(compose.uiTest)
}
tasks.register<JavaExec>("siteScreenshots") {
    group = "documentation"
    description = "Renders the screenshots of the presentation page into site/assets/."
    classpath = screenshots.runtimeClasspath
    mainClass = "io.github.fugeekies.timeflies.desktop.screenshots.SiteScreenshotsKt"
    args(rootProject.file("site/assets").absolutePath)
}

// `./gradlew :desktopApp:run '-Dtimeflies.debug=true'` hands the option to the app (debug panel, fake date);
// the quotes are needed in PowerShell, which otherwise splits the option at the dot.
// Only the local run task gets it: the installer never does (docs/DEVELOPMENT.md, "Panneau de débogage").
val debugOption = providers.systemProperty("timeflies.debug")
tasks.withType<JavaExec>().matching { it.name == "run" }.configureEach {
    debugOption.orNull?.let { systemProperty("timeflies.debug", it) }
}
