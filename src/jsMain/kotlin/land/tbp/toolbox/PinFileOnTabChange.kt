package land.tbp.toolbox

import obsidian.App
import obsidian.EventRef

/**
 * This can be used instead of the plugin Mononote ([https://github.com/czottmann/obsidian-mononote](https://github.com/czottmann/obsidian-mononote)).
 */
fun pinFileOnTabChange(app: App): EventRef =
    app.workspace.on("active-leaf-change", { leaf ->
        leaf ?: return@on
        if(leaf.getViewState().type != "markdown") return@on

        leaf.setPinned(true)
        console.log("pinned leaf", leaf.getViewState(), leaf.getViewState().type)
    })
