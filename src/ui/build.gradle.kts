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
            api(libs.kermit)
        }
        // Contrast of the colour tokens (docs/UI_DESIGN_SPEC.md, "Accessibilité"), on the JVM only.
        getByName("desktopTest").dependencies {
            implementation(kotlin("test"))
        }
    }
}

// The event base lives apart from the code, in assets/events/<family>/<type>/<origin>[/<subcategory>].json
// (docs/EVENTS_DATA.md, decision of 16/11/2026). It is copied into the app resources as files/events/, with
// index.txt listing every file: an app cannot list a folder of its resources. A custom directory replaces the
// module's composeResources folder, so both are gathered into one generated folder.
val eventsSource = rootProject.layout.projectDirectory.dir("assets/events")
val moduleResources = layout.projectDirectory.dir("src/commonMain/composeResources")
val gatheredResources = layout.buildDirectory.dir("generated/composeResources")
val gatherResources = tasks.register<Sync>("gatherResources") {
    from(moduleResources)
    from(eventsSource) {
        include("**/*.json")
        into("files/events")
    }
    into(gatheredResources)
    val source = eventsSource.asFile
    doLast {
        val index = source.walkTopDown().filter { it.isFile && it.extension == "json" }
            .map { it.relativeTo(source).invariantSeparatorsPath.removeSuffix(".json") }
            .sorted().joinToString("\n", postfix = "\n")
        destinationDir.resolve("files/events/index.txt").writeText(index)
    }
}

compose.resources {
    packageOfResClass = "io.github.fugeekies.timeflies.ui.resources"
    customDirectory(sourceSetName = "commonMain", directoryProvider = gatherResources.map { gatheredResources.get() })
}

// UserGuideTest reads the user guide and the README (AGENTS.md, "Guide d'utilisation multilingue"): a change there
// must run the tests again.
tasks.named<Test>("desktopTest") {
    inputs.dir(rootProject.layout.projectDirectory.dir("docs/guide")).withPathSensitivity(PathSensitivity.RELATIVE).withPropertyName("userGuide")
    inputs.file(rootProject.layout.projectDirectory.file("README.md")).withPathSensitivity(PathSensitivity.RELATIVE).withPropertyName("readme")
}
