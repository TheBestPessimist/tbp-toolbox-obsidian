import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotest)
	alias(libs.plugins.ksp)
}

kotlin {
	js {
		outputModuleName = "shared" // todo far in the future: change this module's name

		// Obsidian is an electron application
		// so node seems like a decent choice
		nodejs {
		}

		// Generate executable binary (single .js file containing the whole program)
		binaries.executable()

		// Apparently this ay help with interop and IDE debugging
		generateTypeScriptDefinitions()

		// this compilerOptions block applies ONLY to the JS source set
		compilerOptions {
			// Target ES2015 for modern JavaScript features
			target = "es2015"

			// Use CommonJS module format for Obsidian plugin compatibility
			// This is one of the official ways per https://kotlinlang.org/docs/js-modules.html
			// an alternative to this is to write `js.useCommonJs()` outside of `compilerOptions`
			moduleKind = org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_COMMONJS
		}
	}

	// this compilerOptions block applies to ALL kotlin source sets in this project
	compilerOptions {
		apiVersion = KotlinVersion.KOTLIN_2_2
		languageVersion = KotlinVersion.KOTLIN_2_2
	}

	sourceSets {
		commonMain {
			dependencies {
				api(kotlin("stdlib"))
				implementation(libs.kotlinx.coroutines.core)
                implementation ("io.github.oshai:kotlin-logging:7.0.13")
			}
		}

		commonTest.dependencies {
			implementation(libs.kotest.framework.engine)
			implementation(libs.kotest.assertions.core)
		}

		jsMain.dependencies {
			api(kotlin("stdlib-js"))

			// Use obsidian-api for Obsidian API type declarations
			api(project(":obsidian-api"))

			// implementation(npm("react", "> 14.0.0 <=16.9.0"))
			// implementation(devNpm("@types/node", "^16.11.6"))
			// implementation(devNpm("@typescript-eslint/eslint-plugin", "5.29.0"))
			// implementation(devNpm("@typescript-eslint/parser", "5.29.0"))
			// implementation(devNpm("eslint", "^8.0.0"))
			// implementation(devNpm("tslib", "2.4.0"))
			// implementation(devNpm("typescript", "4.7.4"))
		}

		jsTest.dependencies {
			// Use obsidian-fake for testing (includes obsidian-api + stub implementations)
			implementation(project(":obsidian-fake"))
		}
	}
}



tasks.wrapper {
	gradleVersion = "9.1.0"
	distributionType = Wrapper.DistributionType.ALL
}

// tasks.register<Exec>("dev") {
// 	group = "application"
// 	description = "Compile Kotlin to JS and start the development server"
// 	workingDir = projectDir
//
// 	// 1. Compile Kotlin to JS
// 	dependsOn("jsBrowserDevelopmentLibraryDistribution")
//
// 	// 2. Run npm
// 	// Detect OS and use appropriate command
// 	val isWindows = System.getProperty("os.name").lowercase().contains("windows")
//
// 	if (isWindows) {
// 		commandLine("cmd", "/c", "npm install && npm run start")
// 	} else {
// 		commandLine("sh", "-c", "npm install && npm run start")
// 	}
// }

// tasks.register<Exec>("buildPlugin") {
// 	group = "build"
// 	description = "Build Obsidian plugin: compile Kotlin to JS and copy to main.js"
// 	workingDir = projectDir
//
// 	// 1. Compile Kotlin to JS (development build for faster compilation)
// 	dependsOn("compileDevelopmentExecutableKotlinJs")
//
// 	// 2. Copy the generated shared.js to main.js
// 	val isWindows = System.getProperty("os.name").lowercase().contains("windows")
// 	if (isWindows) {
// 		commandLine("cmd", "/c", "copy", "build\\compileSync\\js\\main\\developmentExecutable\\kotlin\\shared.js", "main.js")
// 	} else {
// 		commandLine("cp", "shared/build/compileSync/js/main/developmentExecutable/kotlin/shared.js", "main.js")
// 	}
// }

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
