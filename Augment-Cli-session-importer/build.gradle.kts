plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "land.tbp"
version = "unspecified"

// dependencies {
//     testImplementation(kotlin("test"))
// }

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
}
