package land.tbp.toolbox.demostuff

import land.tbp.toolbox.empty
import land.tbp.toolbox.setText
import obsidian.App
import obsidian.Modal

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
