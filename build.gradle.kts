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

// Task to create package.json for fake-obsidian
abstract class CreatePackageJsonTask : DefaultTask() {
	@get:OutputFile
	abstract val outputFile: RegularFileProperty

	@TaskAction
	fun create() {
		val packageJsonContent = """
		{
		  "name": "obsidian",
		  "version": "0.0.0-fake",
		  "description": "Fake Obsidian API for testing",
		  "main": "obsidian.js",
		  "type": "commonjs"
		}
		""".trimIndent()

		outputFile.get().asFile.parentFile.mkdirs()
		outputFile.get().asFile.writeText(packageJsonContent)
	}
}

tasks.register<CreatePackageJsonTask>("createFakeObsidianPackageJson") {
	group = "build"
	description = "Create package.json for fake-obsidian module"
	outputFile.set(layout.buildDirectory.file("js/node_modules/obsidian/package.json"))
}

// Task to copy fake-obsidian library to node_modules for testing
tasks.register<Copy>("copyFakeObsidianForTests") {
	group = "build"
	description = "Copy fake-obsidian library to node_modules for testing"

	// Depend on compiling the fake-obsidian library and creating package.json
	dependsOn(":fake-obsidian:compileProductionLibraryKotlinJs")
	dependsOn("createFakeObsidianPackageJson")

	// Copy from fake-obsidian build output
	from("fake-obsidian/build/compileSync/js/main/productionLibrary/kotlin")

	// To the node_modules/obsidian directory
	into("build/js/node_modules/obsidian")

	// Include the main JS file
	include("obsidian.js")
}

// Make test tasks depend on copying the fake obsidian library
tasks.named("jsTestTestDevelopmentExecutableCompileSync") {
	dependsOn("copyFakeObsidianForTests")
}

tasks.named("jsTestTestProductionExecutableCompileSync") {
	dependsOn("copyFakeObsidianForTests")
}
