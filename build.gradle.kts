plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.wrapper {
    gradleVersion = "9.1.0"
    distributionType = Wrapper.DistributionType.ALL
}

// Task to compile Kotlin and run the dev server
tasks.register<Exec>("dev") {
    group = "application"
    description = "Compile Kotlin to JS and start the development server"
    workingDir = projectDir

    // 1. Compile Kotlin to JS
    dependsOn(":shared:jsBrowserDevelopmentLibraryDistribution")

    // 2. Run npm
    // Detect OS and use appropriate command
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")

    if (isWindows) {
        commandLine("cmd", "/c", "npm install && npm run start")
    } else {
        commandLine("sh", "-c", "npm install && npm run start")
    }
}

// Task to build Obsidian plugin (compile Kotlin/JS and bundle with esbuild)
tasks.register<Exec>("buildPlugin") {
    group = "build"
    description = "Build Obsidian plugin: compile Kotlin to JS and bundle into main.js"
    workingDir = projectDir

    // 1. Compile Kotlin to JS (development build for faster compilation)
    dependsOn(":shared:compileDevelopmentExecutableKotlinJs")

    // 2. Bundle with esbuild
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")

    if (isWindows) {
        commandLine("cmd", "/c", "npm install && npm run bundle:dev")
    } else {
        commandLine("sh", "-c", "npm install && npm run bundle:dev")
    }
}

// Task to build production Obsidian plugin
tasks.register<Exec>("buildPluginProduction") {
    group = "build"
    description = "Build production Obsidian plugin: compile Kotlin to JS and bundle into minified main.js"
    workingDir = projectDir

    // 1. Compile Kotlin to JS (production build)
    dependsOn(":shared:compileProductionExecutableKotlinJs")

    // 2. Bundle with esbuild in production mode
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")

    if (isWindows) {
        commandLine("cmd", "/c", "npm install && npm run bundle:prod")
    } else {
        commandLine("sh", "-c", "npm install && npm run bundle:prod")
    }
}
