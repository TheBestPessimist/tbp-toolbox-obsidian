package land.tbp.toolbox.demostuff

import land.tbp.toolbox.MyPlugin
import land.tbp.toolbox.addClass
import land.tbp.toolbox.empty
import land.tbp.toolbox.setText
import obsidian.App
import obsidian.Notice
import obsidian.PluginSettingTab
import obsidian.Setting
import org.w3c.dom.events.MouseEvent

fun otherDemoFeatures(app: App, plugin: MyPlugin) {
    plugin.addSettingTab(SampleSettingTab(app, plugin))

    // Add status bar item
    val statusBarItemEl = plugin.addStatusBarItem()
    statusBarItemEl.setText("Status Bar Text")

    // // Register DOM event
    // plugin.registerDomEvent(kotlinx.browser.document, "click") { evt ->
    //     console.log("click", evt)
    // }

    // Add ribbon icon
    val ribbonIconEl = plugin.addRibbonIcon("dice", "Sample Plugin") { _evt: MouseEvent ->
        Notice("This is a notice!")
    }
    // Add CSS class to ribbon icon
    ribbonIconEl.addClass("my-plugin-ribbon-class")

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

// Create modal instance to use in commands
// val sampleModal = SampleModal(app)

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
