import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		// Set the module name to "obsidian" so it can be required as require("obsidian")
		outputModuleName = "obsidian"

		// Use nodejs target since tests run in Node.js
		nodejs {
		}

		// Generate a library (not executable)
		binaries.library()

		// Generate TypeScript definitions for better IDE support
		generateTypeScriptDefinitions()

		compilerOptions {
			// Target ES2015 for modern JavaScript features
			target = "es2015"

			// Use CommonJS module format to match the main project
			moduleKind = org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_COMMONJS
		}
	}

	// Apply compiler options to all source sets
	compilerOptions {
		apiVersion = KotlinVersion.KOTLIN_2_2
		languageVersion = KotlinVersion.KOTLIN_2_2
	}

	sourceSets {
		jsMain {
			dependencies {
				api(kotlin("stdlib-js"))
			}
		}
	}
}

