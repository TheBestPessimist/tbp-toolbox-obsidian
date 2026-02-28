import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
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
        apiVersion = KotlinVersion.KOTLIN_2_3
        languageVersion = KotlinVersion.KOTLIN_2_3
    }

    sourceSets {
        jsMain {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }
    }
}

