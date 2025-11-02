package land.tbp.toolbox

import obsidian.App
import obsidian.Modal
import obsidian.Notice
import obsidian.Plugin
import obsidian.PluginManifest
import obsidian.PluginSettingTab
import obsidian.Setting
import obsidian.TFile
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

    override fun onload(): Any {
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

        // Create modal instance to use in commands
        val sampleModal = SampleModal(app)

        // // Add simple command - pure Kotlin
        // addCommand(command {
        //     id = "open-sample-modal-simple"
        //     name = "Open sample modal (simple)"
        //     callback = {
        //         sampleModal.open()
        //     }
        // })


        // // Add complex command with check callback - pure Kotlin
        // addCommand(command {
        //     id = "open-sample-modal-complex"
        //     name = "Open sample modal (complex)"
        //     checkCallback = { checking ->
        //         // Get the active MarkdownView using the proper type constructor
        //         val markdownView = app.workspace.getActiveViewOfType(MarkdownView::class.js)
        //         if (markdownView != null) {
        //             if (!checking) {
        //                 sampleModal.open()
        //             }
        //             true
        //         } else {
        //             false
        //         }
        //     }
        // })

        // // Add command to toggle view mode: Source → Preview → ReadOnly
        // addCommand(command {
        //     id = "toggle-view-mode"
        //     name = "Toggle view mode (Source → Preview → ReadOnly)"
        //     checkCallback = fdaasdf()
        // })


        // Add command to toggle view mode: Source → Preview → ReadOnly
        addCommand(cycleViewStateForwardCommand(app))

        registerEvent(pinFileOnTabChange(this@MyPlugin.app))

        // // Add command to collect and print all ViewState types
        // addCommand(command {
        //     id = "collect-viewstate-types"
        //     name = "Collect and print all ViewState types"
        //     callback = {
        //         collectAndPrintViewStateTypes()
        //     }
        // })

        // Add settings tab
        addSettingTab(SampleSettingTab(app, this))

        // Register DOM event
        registerDomEvent(kotlinx.browser.document, "click") { evt ->
            console.log("click", evt)
        }

        // Register interval
        val intervalId = kotlinx.browser.window.setInterval({
            console.log("setInterval")
        }, 5 * 60 * 1000)
        registerInterval(intervalId)
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

    fun saveSettings(): Promise<Unit> {
        return saveData(settings).then {
            Unit
        }
    }

    /**
     * Collect all ViewState.type values by opening one file per extension and checking its type
     */
    private fun collectAndPrintViewStateTypes() {
        val viewStateTypes = mutableSetOf<String>()
        val files = app.vault.getFiles()

        // Filter to get only one file per extension
        val seenExtensions = mutableSetOf<String>()
        val filesToScan = files.filter { file ->
            val extension = file.extension.lowercase()
            if (seenExtensions.contains(extension)) {
                false
            } else {
                seenExtensions.add(extension)
                true
            }
        }

        Notice("Scanning ${filesToScan.size} files (one per extension)... This may take a moment.")
        console.log("=== Starting ViewState.type collection ===")
        console.log("Total files in vault: ${files.size}")
        console.log("Unique extensions found: ${filesToScan.size}")
        console.log("Files to scan: ${filesToScan.size}")

        // Process files sequentially (creating a new leaf for each file)
        processFilesSequentially(filesToScan, 0, viewStateTypes)
    }

    /**
     * Process files one by one to collect ViewState types
     * Creates a fresh leaf for each file to avoid state contamination
     */
    private fun processFilesSequentially(
        files: List<TFile>,
        index: Int,
        viewStateTypes: MutableSet<String>,
    ) {
        if (index >= files.size) {
            // Done processing all files

            // Print results
            console.log("")
            console.log("=== All ViewState.type values found in vault ===")
            console.log("Total unique ViewState types: ${viewStateTypes.size}")
            console.log("Total unique extensions scanned: ${files.size}")
            console.log("")
            console.log("ViewState types (sorted):")
            viewStateTypes.sorted().forEach { type ->
                console.log("  - $type")
            }
            console.log("================================================")

            Notice("Found ${viewStateTypes.size} unique ViewState types from ${files.size} unique extensions. Check console for details.")
            return
        }

        val file = files[index]

        // Create a fresh leaf for this file to avoid state contamination
        val tempLeaf = app.workspace.getLeaf(false)

        // Open the file and get its ViewState type
        tempLeaf.openFile(file).then {
            // Add a small delay to ensure the view has fully loaded
            Promise<Unit> { resolve, _ ->
                kotlinx.browser.window.setTimeout({
                    resolve(Unit)
                }, 50)
            }.then {
                val viewState = tempLeaf.getViewState()
                val viewType = viewState.type
                viewStateTypes.add(viewType)

                console.log("Extension: .${file.extension} -> ViewState.type: '$viewType' (file: ${file.path})")

                // Close this leaf before processing the next file
                tempLeaf.detach()

                // Process next file
                processFilesSequentially(files, index + 1, viewStateTypes)
            }
        }.catch { error ->
            console.error("Error opening file ${file.path}:", error)
            // Close the leaf and continue with next file even if this one failed
            tempLeaf.detach()
            processFilesSequentially(files, index + 1, viewStateTypes)
        }
    }
}






/**
 * Sample Modal dialog - pure Kotlin implementation
 */
class SampleModal(app: App) : Modal(app) {
    override fun onOpen() {
        contentEl.setText("Woah!")
    }

    override fun onClose() {
        contentEl.empty()
    }
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
