// Performance benchmarks of data loading and of the computations behind each page.
// Not part of `check`: run with `./gradlew :benchmarks:benchmark` (also run by the CI on every PR).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(projects.domain)
    testImplementation(projects.data)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(kotlin("test"))
}

val eventsDir = rootProject.layout.projectDirectory.dir("src/ui/src/commonMain/composeResources/files/events")
val reportFile = layout.buildDirectory.file("reports/benchmarks/performance-report.md")

// The regular test task stays empty so that `check` does not run the benchmarks.
tasks.test {
    enabled = false
}

tasks.register<Test>("benchmark") {
    description = "Runs the performance benchmarks and writes build/reports/benchmarks/performance-report.md."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnit()
    systemProperty("timeflies.eventsDir", eventsDir.asFile.absolutePath)
    systemProperty("timeflies.benchmarkReport", reportFile.get().asFile.absolutePath)
    // Same heap on every machine, so results stay comparable.
    maxHeapSize = "1g"
    outputs.upToDateWhen { false }
    testLogging { showStandardStreams = true }
}
