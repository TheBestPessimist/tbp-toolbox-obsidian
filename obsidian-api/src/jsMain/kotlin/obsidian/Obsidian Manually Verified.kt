@file:JsModule("obsidian")

package obsidian

abstract external class View : Component


external class MarkdownView : View {
    var app: App
    var leaf: WorkspaceLeaf
}

open external class GoodWorkspace : Events {
    fun <T : View> getActiveViewOfType(type: JsClass<T>): T?
}

open external class Events
