package land.tbp.toolbox

import land.tbp.toolbox.demostuff.otherDemoFeatures

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
