package land.tbp.toolbox

import land.tbp.toolbox.demostuff.SampleModal
import obsidian.App
import obsidian.Notice
import obsidian.Plugin
import obsidian.PluginManifest
import obsidian.PluginSettingTab
import obsidian.Setting
import org.w3c.dom.events.MouseEvent
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

    // Set to track all ViewState.type values encountered
    private val viewStateTypes = mutableSetOf<String>()

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

    private fun setupPlugin() {
        // Add ribbon icon
        val ribbonIconEl = addRibbonIcon("dice", "Sample Plugin") { _evt: MouseEvent ->
            Notice("This is a notice!")
        }
        // Add CSS class to ribbon icon
        ribbonIconEl.addClass("my-plugin-ribbon-class")

        // Add status bar item
        val statusBarItemEl = addStatusBarItem()
        statusBarItemEl.setText("Status Bar Text")


        // Add command to toggle view mode: Source → Preview → ReadOnly
        addCommand(cycleViewStateForwardCommand(app))

        registerEvent(pinFileOnTabChange(this@MyPlugin.app))

        // Add settings tab
        addSettingTab(SampleSettingTab(app, this))

        // Register DOM event
        registerDomEvent(kotlinx.browser.document, "click") { evt ->
            console.log("click", evt)
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

/**
 * Sample Settings Tab - pure Kotlin implementation
 */
class SampleSettingTab(app: App, val myPlugin: MyPlugin) : PluginSettingTab(app, myPlugin) {

    override fun display() {
        containerEl.empty()

        Setting(containerEl)
            .setName("Setting #1")
            .setDesc("It's a secret")
            .addText { text ->
                text
                    .setPlaceholder("Enter your secret")
                    .setValue(myPlugin.settings.mySetting)
                    .onChange { value ->
                        myPlugin.settings.mySetting = value
                        myPlugin.saveSettings()
                    }
            }
    }
}
