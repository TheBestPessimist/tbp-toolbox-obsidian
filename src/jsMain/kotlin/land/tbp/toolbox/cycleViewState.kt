package land.tbp.toolbox

import jso
import kotlinx.coroutines.NonCancellable.key
import obsidian.App
import obsidian.Hotkey
import obsidian.MarkdownView
import obsidian.Notice
import obsidian.ViewState
import obsidian.WorkspaceLeaf

fun cycleViewStateForwardCommand(app: App): Command = Command(
    id = "cycle-view-mode-forward",
    name = "Cycle view mode forward: Source → Preview → ReadOnly",
    callback = {
        val markdownView = app.workspace.getActiveViewOfType(MarkdownView::class.js) ?: return@Command null
        cycleViewStateCallback(markdownView.leaf)
    },
    hotkeys = arrayOf(
        jso<Hotkey>().apply { modifiers = arrayOf("Mod"); key = "E" }  // TODO tbp: make modifiers an enum
    )
)

/**
 * Related:
 * - https://forum.obsidian.md/t/easily-switch-between-source-mode-live-preview-preview/27151/15
 * - https://github.com/Signynt/obsidian-editing-mode-hotkey/blob/master/main.ts
 * - https://github.com/dk949/obsidian-mode-manager/blob/trunk/src/main.ts
 */
private fun cycleViewStateCallback(leaf: WorkspaceLeaf) {
    val viewState = leaf.getViewState()

    // Only toggle for Markdown views
    if (viewState.type != "markdown") { // todo make this an enum of sorts
        return
    }

    console.log(viewState)
    val nonRetardedViewState = NonRetardedViewState.valueOf(viewState)
    val nextViewState = nonRetardedViewState.getNextState()
    viewState.state.apply {
        source = nextViewState.source
        mode = nextViewState.mode
    }
    leaf.setViewState(viewState)
    console.log("cycleViewState: $nonRetardedViewState -> $nextViewState")
    Notice(nextViewState.toString())
}

/**
 * The way Obsidian represents the 3 different view states is fucking retarded.
 * So i had to create this fucking enum to deal with this shit.
 *
 * - Source mode has `mode="source"` and `source=true`
 * - Preview mode has `mode="source"` and `source=false`
 * - ReadOnly mode has `mode="preview"` and `source=ignored` (can be `true` or `false`)
 */
enum class NonRetardedViewState(val mode: String, val source: Boolean) {
    Source("source", true),
    Preview("source", false),
    ReadOnly("preview", true),
    ;

    companion object {
        fun valueOf(viewState: ViewState): NonRetardedViewState {
            val state = viewState.state

            val s: Pair<String, Boolean> = Pair(state.mode, state.source)
            return when (s) {
                "source" to true -> Source
                "source" to false -> Preview
                "preview" to true -> ReadOnly
                "preview" to false -> ReadOnly
                else -> error("unknown view state: $viewState")
            }
        }
    }

    fun getNextState(): NonRetardedViewState = when (this) {
        Source -> Preview
        Preview -> ReadOnly
        ReadOnly -> Source
    }
}
