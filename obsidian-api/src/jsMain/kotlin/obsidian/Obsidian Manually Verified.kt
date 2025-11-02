@file:JsModule("obsidian")

package obsidian

abstract external class View : Component


external class MarkdownView : View {
    var app: App
    var leaf: WorkspaceLeaf
}

open external class GoodWorkspace : Events {
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
