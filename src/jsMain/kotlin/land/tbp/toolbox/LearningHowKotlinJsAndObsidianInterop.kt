package land.tbp.toolbox

import land.tbp.toolbox.demostuff.otherDemoFeatures
import obsidian.App
import obsidian.Plugin
import obsidian.PluginManifest
import kotlin.js.Promise


fun TbpToolboxPlugin.setupPlugin() {
    // Add command to cycle view mode: Source → Preview → ReadOnly and backwards
    addCommand(cycleViewStateForwardCommand(app))
    addCommand(cycleViewStateBackwardCommand(app))


    registerEvent(pinFileOnTabChange(app))

    // Add settings tab
    otherDemoFeatures(app, this)
}

// interface MyPluginSettings {
//     var mySetting: String
// }

// class DefaultSettings : MyPluginSettings {
//     override var mySetting: String = "default"
// }
