plugins {
	alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
	js(IR) {
		outputModuleName = "shared"

		// Obsidian runs in Electron (browser-like environment)
		// Using browser target for proper environment
		browser {
			// No webpack needed - we're using whole-program compilation
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


	sourceSets {
		all {
			languageSettings.apply {
				languageVersion = "2.2"
				apiVersion = "2.2"
			}
		}
		commonMain.dependencies {
			// Coroutines support for async/await
			implementation(libs.kotlinx.coroutines.core)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
		commonTest.dependencies {
			implementation(kotlin("test")) // Brings all the platform dependencies automatically
		}
		jsMain.dependencies {
			implementation(npm("react", "> 14.0.0 <=16.9.0"))
			implementation(devNpm("@types/node", "^16.11.6"))
			implementation(devNpm("@typescript-eslint/eslint-plugin", "5.29.0"))
			implementation(devNpm("@typescript-eslint/parser", "5.29.0"))
			implementation(devNpm("obsidian", "latest"))
			implementation(devNpm("tslib", "2.4.0"))
			implementation(devNpm("typescript", "4.7.4"))
		}
	}
}
