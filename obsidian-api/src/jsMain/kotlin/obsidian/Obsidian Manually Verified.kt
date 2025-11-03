@file:JsModule("obsidian")

package obsidian

abstract external class View : Component

external class MarkdownView : View {
    var app: App
    var leaf: WorkspaceLeaf
}

/**
 * In `obsidian.d.ts` this is: `export type Constructor<T> = abstract new (...args: any[]) => T;`
 *
 * In Kotlin/JS, the `Constructor<T>` parameter becomes `JsClass<T>`.
 */
@Deprecated(
    message = "Do not use this. Use `JsClass<T>` instead.",
    replaceWith = ReplaceWith("JsClass<T>", "kotlin.js.JsClass"),
)
typealias Constructor<T> = JsClass<T>

open external class GoodWorkspace : Events {
    /**
     * Get the currently active view of a given type.
     */
    fun <T : View> getActiveViewOfType(type: JsClass<T>): T?
    fun on(name: String, callback: (leaf: WorkspaceLeaf?) -> Unit, ctx: Any? = definedExternally): EventRef
}

open external class Events

/**
 * An event reference.
 * Similar to a `hwnd` in Autohotkey.
 */
external interface EventRef

open external class Component {
    /**
     * Registers an event to be detached when unloading
     */
    fun registerEvent(eventRef: EventRef)
}

external class App {
    val workspace: Workspace
    val vault: Vault
}

/**
 * Represents a hotkey modifier.
 *
 * - Mod == Ctrl
 * - Meta
 * - Shift
 * - Alt
 *
 * TODO: make an enum or something out of the modifier keys
 */
typealias Modifier = String

external interface Hotkey {
    var modifiers: Array<Modifier>
    var key: String
}

/**
 * Metadata about a Community plugin.
 * @see {@link https://docs.obsidian.md/Reference/Manifest}
 */
external interface PluginManifest {
    var id: String
    var name: String
    var author: String
    var version: String

    /**
     * The minimum required Obsidian version to run this plugin.
     */
    var minAppVersion: String
    var description: String
    var authorUrl: String?
    var isDesktopOnly: Boolean?

    /**
     * Vault path to the plugin folder in the config directory.
     */
    var dir: String?
}

/**
 * Icon ID to be used in the toolbar.
 *
 * See [https://docs.obsidian.md/Plugins/User+interface/Icons](https://docs.obsidian.md/Plugins/User+interface/Icons) for available icons and how to add your own.
 */
typealias IconName = String

external interface Command {
    /**
     * Globally unique ID to identify this command.
     */
    var id: String

    /**
     * Human-friendly name for searching.
     */
    var name: String
    var icon: IconName?
    var mobileOnly: Boolean?

    /**
     * Whether holding the hotkey should repeatedly trigger this command.
     *
     * defaultValue = false
     */
    var repeatable: Boolean?
    var callback: (() -> Any?)?

    /**
     * Complex callback, overrides the simple callback.
     * Used to 'check' whether your command can be performed in the current circumstances.
     * For example, if your command requires the active focused pane to be a MarkdownView, then
     * you should only return true if the condition is satisfied. Returning false or undefined causes
     * the command to be hidden from the command palette.
     *
     * **TS Example** - TODO make kotlin
     * ```ts
     * this.addCommand({
     *   id: 'example-command',
     *   name: 'Example command',
     *   checkCallback: (checking: boolean) => {
     *     const value = getRequiredValue();
     *
     *     if (value) {
     *       if (!checking) {
     *         doCommand(value);
     *       }
     *       return true;
     *     }
     *
     *     return false;
     *   }
     * });
     * ```
     *
     *
     * @param checking Whether the command palette is just 'checking' if your command should show right now.
     * If checking is true, then this function should not perform any action.
     * If checking is false, then this function should perform the action.
     *
     * @return Whether this command can be executed at the moment.
     */
    var checkCallback: ((checking: Boolean) -> Boolean)?

    /**
     * A command callback that is only triggered when the user is in an editor.
     *
     * Overrides [callback] and [checkCallback]
     *
     * **TS Example** - TODO make kotlin
     * ```ts
     * this.addCommand({
     *   id: 'example-command',
     *   name: 'Example command',
     *   editorCallback: (editor: Editor, view: MarkdownView) => {
     *     const sel = editor.getSelection();
     *
     *     console.log(`You have selected: ${sel}`);
     *   }
     * });
     * ```
     */
    var editorCallback: ((editor: Editor, ctx: Any /* MarkdownView | MarkdownFileInfo */) -> Any)?

    /**
     * A command callback that is only triggered when the user is in an editor.
     *
     * Overrides [editorCallback], [callback] and [checkCallback]
     *
     * **TS Example** - TODO make kotlin
     * ```ts
     * this.addCommand({
     *   id: 'example-command',
     *   name: 'Example command',
     *   editorCheckCallback: (checking: boolean, editor: Editor, view: MarkdownView) => {
     *     const value = getRequiredValue();
     *
     *     if (value) {
     *       if (!checking) {
     *         doCommand(value);
     *       }
     *
     *       return true;
     *     }
     *
     *     return false;
     *   }
     * });
     * ```
     */
    var editorCheckCallback: ((checking: Boolean, editor: Editor, ctx: Any /* MarkdownView | MarkdownFileInfo */) -> Boolean)?

    /**
     * Sets the default hotkey.
     */
    var hotkeys: Array<Hotkey>?
}

external class Editor

external interface ViewState {
    /**
     * todo make this somehow a proper type
     * The type of the current file:
     * - `.md` -> `markdown`
     * - `.base` -> `bases`
     * - `.canvas` -> `canvas`
     * - `.png` -> `image`
     * - `.jpg` -> `image`
     * - `.sns` -> `null`
     * - `.log` -> `null`
     * - `.gpx` -> `null`
     *
     * The tricky part here is that there are other types too. Every possible leaf has a type.
     * - `git-history-view` - from the git plugin
     * - `git-view`  - from the git plugin
     * - `tag` - the obsidian tag
     * - `footnotes`
     * - `all-properties`
     * etc.
     */
    var type: String

    /**
     * Technical: This is a dynamic type.
     * As far as i can see, if i console.log(viewState), it outputs all values, even of those which i don't yet have in my Kotlin code.
     * Neat!
     */
    var state: State

    interface State {
        var mode: String
        var source: Boolean
    }
}
