rootProject.name = "TimeFlies"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Code modules live under src/, apart from the docs and the build files.
listOf("domain", "data", "ui", "androidApp", "desktopApp", "benchmarks").forEach { name ->
    include(":$name")
    project(":$name").projectDir = file("src/$name")
}
