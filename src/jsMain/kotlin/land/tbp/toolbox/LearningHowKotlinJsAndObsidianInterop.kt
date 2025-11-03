package land.tbp.toolbox

import land.tbp.toolbox.demostuff.otherDemoFeatures
import obsidian.App
import obsidian.Plugin
import obsidian.PluginManifest
import kotlin.js.Promise

interface MyPluginSettings {
    var mySetting: String
}

class DefaultSettings : MyPluginSettings {
    override var mySetting: String = "default"
}

/**
 * Main plugin class
 * This is exported via the root-level ObsidianPlugin wrapper class
 */
open class MyPlugin(app: App, manifest: PluginManifest) : Plugin(app, manifest) {
    var settings: MyPluginSettings = DefaultSettings()

    override fun onload(): Promise<Unit> {
        console.log("Loading MyPlugin in Kotlin!")

        // Return a promise for async loading
        return Promise { resolve, reject ->
            try {
                // Load settings first
                loadSettings().then {
                    setupPlugin()
                    resolve(Unit)
                }.catch { error ->
                    console.error("Error loading settings:", error)
                    reject(error)
                }
            } catch (e: Throwable) {
                console.error("Error in onload:", e)
                reject(e)
            }
        }
    }

    override fun onunload() {
        console.log("Unloading MyPlugin")
    }

    private fun loadSettings(): Promise<Unit> {
        return loadData().then { data ->
            if (data != null) {
                // Merge loaded data with default settings
                val defaultSettings = DefaultSettings()
                settings = mergeSettings(defaultSettings, data)
            }
            Unit
        }
    }

    private fun mergeSettings(defaults: MyPluginSettings, loaded: Any): MyPluginSettings {
        // Safely extract the mySetting property from the loaded data
        val loadedMySetting = try {
            loaded.asDynamic().mySetting as? String
        } catch (e: Throwable) {
            null
        }

        return object : MyPluginSettings {
            override var mySetting: String = loadedMySetting?.takeIf { it.isNotEmpty() } ?: defaults.mySetting
        }
    }

    fun saveSettings(): Promise<Unit> = saveData(settings)
}

fun MyPlugin.setupPlugin() {
    // Add command to cycle view mode: Source → Preview → ReadOnly and backwards
    addCommand(cycleViewStateForwardCommand(app))
    addCommand(cycleViewStateBackwardCommand(app))


    registerEvent(pinFileOnTabChange(app))

    // Add settings tab
    otherDemoFeatures(app, this)
}
