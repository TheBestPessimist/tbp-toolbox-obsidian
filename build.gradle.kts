plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinMultiplatform) apply false
}

// Task to compile Kotlin and run the dev server
tasks.register<Exec>("dev") {
    group = "application"
    description = "Compile Kotlin to JS and start the development server"

    // Step 1: Compile Kotlin to JS
    dependsOn(":shared:jsBrowserDevelopmentLibraryDistribution")

    // Step 2 & 3: Run npm install and start dev server
    // Detect OS and use appropriate command
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")

    if (isWindows) {
        commandLine("cmd", "/c", "npm install && npm run start")
    } else {
        commandLine("sh", "-c", "npm install && npm run start")
    }

    workingDir = projectDir
}
