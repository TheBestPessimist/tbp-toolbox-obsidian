plugins {
    alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
    js {
        outputModuleName = "shared"
        browser()
        this.nodejs()
        // binaries.library()
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
        commonTest.dependencies {
            implementation(kotlin("test")) // Brings all the platform dependencies automatically
        }
        jsMain.dependencies {
            implementation(npm("react", "> 14.0.0 <=16.9.0"))
        }
    }
}
