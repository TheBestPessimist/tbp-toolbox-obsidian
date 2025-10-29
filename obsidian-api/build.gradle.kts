import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		// This is just an API definition library, not a runnable module
		// We don't set outputModuleName because this won't be compiled to a standalone module
		
		// Use nodejs target for compatibility
		nodejs {
		}

		// This is a library of external declarations only
		binaries.library()

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

