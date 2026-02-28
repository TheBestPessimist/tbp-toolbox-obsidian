rootProject.name = "tbp-toolbox"
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
}

// Include the obsidian-api subproject (external API declarations)
include("obsidian-api")

// Include the obsidian-fake subproject (test implementations)
include("obsidian-fake")

include("Augment-Cli-session-importer")