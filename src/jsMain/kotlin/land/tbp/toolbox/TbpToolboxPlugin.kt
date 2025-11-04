package land.tbp.toolbox

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.promise
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

    // Note that using `MainScope()` does not work!
    private val pluginScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onload(): Promise<Unit> {
        console.log("Loading TbpToolboxPlugin in Kotlin!")

        return pluginScope.promise {
            // loadSettings()
            setupPlugin()
            // delay(10.seconds)
        }.catch {
            console.error("Error loading TbpToolboxPlugin", it)
        }
    }

    override fun onunload() {
        console.log("Unloading TbpToolboxPlugin")
        pluginScope.cancel()
    }

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
