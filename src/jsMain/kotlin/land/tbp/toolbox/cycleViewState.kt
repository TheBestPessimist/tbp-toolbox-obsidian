package land.tbp.toolbox

import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import obsidian.App
import obsidian.Command
import obsidian.MarkdownView
import obsidian.Notice
import obsidian.ViewState
import obsidian.WorkspaceLeaf

fun cycleViewStateForwardCommand(app: App): Command = jso {
    id = "cycle-view-mode-forward"
    name = "Cycle view mode forward: Source → Preview → ReadOnly"
    callback = fun() {
        val markdownView = app.workspace.getActiveViewOfType(MarkdownView::class.js) ?: return
        cycleViewStateForwardCallback(markdownView.leaf)
    }
    hotkeys = arrayOf(
        jso { modifiers = arrayOf("Mod"); key = "E" },  // TODO tbp: make modifiers an enum
    )
}

/**
 * Related:
 * - https://forum.obsidian.md/t/easily-switch-between-source-mode-live-preview-preview/27151/15
 * - https://github.com/Signynt/obsidian-editing-mode-hotkey/blob/master/main.ts
 * - https://github.com/dk949/obsidian-mode-manager/blob/trunk/src/main.ts
 */
private fun cycleViewStateForwardCallback(leaf: WorkspaceLeaf) {
    val viewState = leaf.getViewState()

    if (viewState.type != "markdown") return

    // console.log(viewState)
    val nonRetardedViewState = NonRetardedViewState.valueOf(viewState)
    val nextViewState = nonRetardedViewState.getNextState()
    viewState.state.apply {
        source = nextViewState.source
        mode = nextViewState.mode
    }
    leaf.setViewState(viewState)

    // need to focus after switching from ReadOnly to any other view state. IDK why.
    // See https://forum.obsidian.md/t/easily-switch-between-source-mode-live-preview-preview/27151/17?u=thebestpessimist
    (leaf.view as MarkdownView).editor.focus()

    console.log("cycleViewStateForward: $nonRetardedViewState -> $nextViewState")
    Notice(nextViewState.toString())



    KotlinLoggingConfiguration.logLevel = Level.TRACE
    log().log.info { "log info" }
    // log.trace { "log trace" }
    // log.debug { "log debug" }
    // log.error { "log error" }
    // log.warn { "log warn" }
}

fun cycleViewStateBackwardCommand(app: App): Command = jso {
    id = "cycle-view-mode-backward"
    name = "Cycle view mode backward: ReadOnly → Preview → Source"
    callback = fun() {
        val markdownView = app.workspace.getActiveViewOfType(MarkdownView::class.js) ?: return
        cycleViewStateBackwardCallback(markdownView.leaf)
    }
    hotkeys = arrayOf(
        jso { modifiers = arrayOf("Mod", "Shift"); key = "E" },
    )
}

private fun cycleViewStateBackwardCallback(leaf: WorkspaceLeaf) {
    val viewState = leaf.getViewState()

    if (viewState.type != "markdown") return

    val nonRetardedViewState = NonRetardedViewState.valueOf(viewState)
    val prevViewState = nonRetardedViewState.getPreviousState()
    viewState.state.apply {
        source = prevViewState.source
        mode = prevViewState.mode
    }

    leaf.setViewState(viewState)

    // need to focus after switching from ReadOnly to any other view state. IDK why.
    // See https://forum.obsidian.md/t/easily-switch-between-source-mode-live-preview-preview/27151/17?u=thebestpessimist
    (leaf.view as MarkdownView).editor.focus()

    console.log("cycleViewStateBackward: $nonRetardedViewState -> $prevViewState")
    Notice(prevViewState.toString())
}

/**
 * The way Obsidian represents the 3 different view states is fucking retarded.
 * So I had to create this fucking enum to deal with this shit.
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

    fun getPreviousState(): NonRetardedViewState = when (this) {
        Source -> ReadOnly
        Preview -> Source
        ReadOnly -> Preview
    }
}
