package land.tbp.toolbox

import obsidian.MarkdownView
import obsidian.Notice
import obsidian.ViewState

/**
 * Related:
 * - https://forum.obsidian.md/t/easily-switch-between-source-mode-live-preview-preview/27151/15
 * - https://github.com/Signynt/obsidian-editing-mode-hotkey/blob/master/main.ts
 * - https://github.com/dk949/obsidian-mode-manager/blob/trunk/src/main.ts
 */
fun cycleViewState(markdownView: MarkdownView) {
    val viewState = markdownView.leaf.getViewState()

    // Only toggle for Markdown views
    if (viewState.type != "markdown") {
        return
    }

    console.log(viewState)
    val nonRetardedViewState = NonRetardedViewState.valueOf(viewState)
    val nextViewState = nonRetardedViewState.getNextState()
    viewState.state.apply {
        source = nextViewState.source
        mode = nextViewState.mode
    }
    markdownView.leaf.setViewState(viewState)
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
