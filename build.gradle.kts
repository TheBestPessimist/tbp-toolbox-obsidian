import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotest)
	alias(libs.plugins.ksp)
}

kotlin {
	js {
		outputModuleName = "shared"

		// Obsidian runs in Electron (browser-like environment)
		// Using browser target for proper environment
		browser {
		}

		// // Use Node.js for running tests
		nodejs {
		}

		// Generate executable binary (single .js file with whole-program granularity)
		binaries.executable()

		// Generate TypeScript definitions for better IDE support
		generateTypeScriptDefinitions()

		// Compiler options following official Kotlin/JS documentation
		compilerOptions {
			// Target ES2015 for modern JavaScript features
			target = "es2015"

			// Use CommonJS module format for Obsidian plugin compatibility
			// This is the official way per https://kotlinlang.org/docs/js-modules.html
			moduleKind = org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_COMMONJS
		}
	}

	compilerOptions {
		apiVersion = KotlinVersion.KOTLIN_2_2
		languageVersion = KotlinVersion.KOTLIN_2_2
	}

	sourceSets {
		commonMain {
			dependencies {
				api(kotlin("stdlib"))
				implementation(libs.kotlinx.coroutines.core)
			}
		}

		commonTest.dependencies {
			implementation(libs.kotest.framework.engine)
			implementation(libs.kotest.assertions.core)
		}
		jsMain.dependencies {
			api(kotlin("stdlib-js"))
			implementation(npm("react", "> 14.0.0 <=16.9.0"))
			implementation(devNpm("@types/node", "^16.11.6"))
			implementation(devNpm("@typescript-eslint/eslint-plugin", "5.29.0"))
			implementation(devNpm("@typescript-eslint/parser", "5.29.0"))
			implementation(devNpm("eslint", "^8.0.0"))
			implementation(devNpm("obsidian", "latest"))
			implementation(devNpm("@codemirror/state", "6.5.0"))
			implementation(devNpm("@codemirror/view", "6.38.1"))
			implementation(devNpm("tslib", "2.4.0"))
			implementation(devNpm("typescript", "4.7.4"))
		}
	}
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
	dependsOn("jsBrowserDevelopmentLibraryDistribution")

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
	dependsOn("compileDevelopmentExecutableKotlinJs")

	// 2. Copy the generated shared.js to main.js
	val isWindows = System.getProperty("os.name").lowercase().contains("windows")
	if (isWindows) {
		commandLine("cmd", "/c", "copy", "build\\compileSync\\js\\main\\developmentExecutable\\kotlin\\shared.js", "main.js")
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
	dependsOn("compileProductionExecutableKotlinJs")

	// 2. Copy the generated shared.js to main.js
	val isWindows = System.getProperty("os.name").lowercase().contains("windows")
	if (isWindows) {
		// commandLine("cmd", "/c", "copy", "build\\compileSync\\js\\main\\productionExecutable\\kotlin\\shared.js", "main.js")
		commandLine("cmd", "/c", "copy", "build\\compileSync\\js\\main\\productionExecutable\\kotlin\\shared.js", "D:\\all\\notes\\.obsidian\\plugins\\tbp-toolbox\\main.js")
	} else {
		commandLine("cp", "shared/build/compileSync/js/main/productionExecutable/kotlin/shared.js", "main.js")
	}
}
