rootProject.name = "tbp-toolbox-obsidian"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
		mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
		mavenLocal()
        mavenCentral()
    }

    // See https://github.com/JetBrains/kotlin-wrappers/blob/master/README.md#using-in-your-projects
    versionCatalogs {
        create("kotlinWrappers") {
            val wrappersVersion = "2026.2.22"
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
        }
    }
}

// Include the obsidian-api subproject (external API declarations)
include("obsidian-api")

// Include the obsidian-fake subproject (test implementations)
include("obsidian-fake")

include("Augment-Cli-session-importer")
