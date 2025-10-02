plugins {
    alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
    js {
        outputModuleName = "shared"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
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
            // put your Multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
