plugins {
	alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
	js {
		outputModuleName = "shared"
		nodejs {
		}
		binaries.executable()
		generateTypeScriptDefinitions()
		// Use CommonJS module format for Obsidian plugin compatibility
		useCommonJs()
		compilerOptions {
			target = "es2015"
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
			implementation(devNpm("builtin-modules", "3.3.0"))
			implementation(devNpm("esbuild", "0.17.3"))
			implementation(devNpm("obsidian", "latest"))
			implementation(devNpm("tslib", "2.4.0"))
			implementation(devNpm("typescript", "4.7.4"))
		}
	}
}
