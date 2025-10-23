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

// Task to build Obsidian plugin (compile Kotlin/JS to main.js)
// Pure Gradle/Kotlin - no esbuild needed!
// The kotlin.js.ir.output.granularity=whole-program setting in gradle.properties
// makes the Kotlin compiler generate a single self-contained .js file
tasks.register<Exec>("buildPlugin") {
    group = "build"
    description = "Build Obsidian plugin: compile Kotlin to JS and copy to main.js"
    workingDir = projectDir

    // 1. Compile Kotlin to JS (development build for faster compilation)
    dependsOn(":shared:compileDevelopmentExecutableKotlinJs")

    // 2. Copy the generated shared.js to main.js
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    if (isWindows) {
        commandLine("cmd", "/c", "copy", "shared\\build\\compileSync\\js\\main\\developmentExecutable\\kotlin\\shared.js", "main.js")
    } else {
        commandLine("cp", "shared/build/compileSync/js/main/developmentExecutable/kotlin/shared.js", "main.js")
    }
}

// Task to build production Obsidian plugin
// Pure Gradle/Kotlin - no esbuild needed!
tasks.register<Exec>("buildPluginProduction") {
    group = "build"
    description = "Build production Obsidian plugin: compile Kotlin to JS and copy to main.js"
    workingDir = projectDir

    // 1. Compile Kotlin to JS (production build)
    dependsOn(":shared:compileProductionExecutableKotlinJs")

    // 2. Copy the generated shared.js to main.js
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    if (isWindows) {
        commandLine("cmd", "/c", "copy", "shared\\build\\compileSync\\js\\main\\productionExecutable\\kotlin\\shared.js", "main.js")
    } else {
        commandLine("cp", "shared/build/compileSync/js/main/productionExecutable/kotlin/shared.js", "main.js")
    }
}
