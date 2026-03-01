plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

group = "land.tbp"
version = "unspecified"

kotlin {
    js {
        // Obsidian is an electron application
        // so node seems like a decent choice
        nodejs {
        }
        // Generate executable binary (single .js file containing the whole program)
        binaries.executable()
    }

    // this compilerOptions block applies to ALL kotlin source sets in this project
    // compilerOptions {
    //     apiVersion = KotlinVersion.KOTLIN_2_3
    //     languageVersion = KotlinVersion.KOTLIN_2_3
    // }

    sourceSets {
        jsMain {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(libs.kotlinx.io.core)
                implementation(kotlinWrappers.node)
                implementation(libs.kotlinx.serialization)
            }
        }

        jsTest {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
            }
        }
    }
}
