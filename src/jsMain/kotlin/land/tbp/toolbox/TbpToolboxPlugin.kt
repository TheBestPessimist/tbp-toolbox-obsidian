package land.tbp.toolbox

import obsidian.App
import obsidian.Plugin
import obsidian.PluginManifest
import kotlin.js.Promise

/**
 * Main plugin class
 * This is exported via the root-level ObsidianPlugin wrapper class
 */
open class TbpToolboxPlugin(app: App, manifest: PluginManifest) : Plugin(app, manifest) {
    // var settings: MyPluginSettings = DefaultSettings()

    override fun onload(): Promise<Unit> {
        console.log("Loading TbpToolboxPlugin in Kotlin!")

        return Promise { resolve, reject ->
            // loadSettings()
            setupPlugin()
        }
    }

    override fun onunload() = console.log("Unloading TbpToolboxPlugin")

    // private fun loadSettings(): Promise<Unit> {
    //     return loadData().then { data ->
    //         if (data != null) {
    //             // Merge loaded data with default settings
    //             val defaultSettings = DefaultSettings()
    //             settings = mergeSettings(defaultSettings, data)
    //         }
    //         Unit
    //     }
    // }

    // private fun mergeSettings(defaults: MyPluginSettings, loaded: Any): MyPluginSettings {
    //     // Safely extract the mySetting property from the loaded data
    //     val loadedMySetting = try {
    //         loaded.asDynamic().mySetting as? String
    //     } catch (e: Throwable) {
    //         null
    //     }
    //
    //     return object : MyPluginSettings {
    //         override var mySetting: String = loadedMySetting?.takeIf { it.isNotEmpty() } ?: defaults.mySetting
    //     }
    // }

    // fun saveSettings(): Promise<Unit> = saveData(settings)
}
