// Obsidian API definitions for Kotlin/JS, generated  by LLMs from obsidian.d.ts

@file:JsModule("obsidian")

package obsidian

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise

open external class Workspace : GoodWorkspace {
    /**
     * TODO I'm not sure this works as i expect it to work. The docs are confusing AF!
     *
     * getLeaf(newLeaf?: PaneType | boolean): WorkspaceLeaf;
     *
     * If newLeaf is false (or not set) then an existing leaf which can be navigated
     * is returned, or a new leaf will be created if there was no leaf available.
     *
     * If newLeaf is `'tab'` or `true` then a new leaf will be created in the preferred
     * location within the root split and returned.
     *
     * If newLeaf is `'split'` then a new leaf will be created adjacent to the currently active leaf.
     *
     * If newLeaf is `'window'` then a popout window will be created with a new leaf inside.
     *
     * @public
     * @since 0.16.0
     */
    fun getLeaf(newLeaf: Boolean = definedExternally): WorkspaceLeaf
}

external class Vault {
    fun getFiles(): Array<TFile>
    fun getMarkdownFiles(): Array<TFile>
    fun getAllLoadedFiles(): Array<dynamic>
}

external class TFile {
    var path: String
    var name: String
    var extension: String
    var basename: String
}

abstract external class Plugin(app: App, manifest: PluginManifest) : GoodPlugin {
    fun loadData(): Promise<Any?>
    fun saveData(data: Any?): Promise<Unit>
}

open external class Modal(app: App) {
    var app: App
    var contentEl: HTMLElement

    open fun onOpen()
    open fun onClose()
    fun open()
    fun close()
}

open external class PluginSettingTab(app: App, plugin: Plugin) {
    var app: App
    var plugin: Plugin
    var containerEl: HTMLElement

    open fun display()
    open fun hide()
}

external class Setting(containerEl: HTMLElement) {
    var settingEl: HTMLElement
    var infoEl: HTMLElement
    var nameEl: HTMLElement
    var descEl: HTMLElement
    var controlEl: HTMLElement

    fun setName(name: String): Setting
    fun setDesc(desc: String): Setting
    fun setHeading(): Setting
    fun setDisabled(disabled: Boolean): Setting
    fun addText(callback: (text: TextComponent) -> Any): Setting
    fun addToggle(callback: (toggle: ToggleComponent) -> Any): Setting
    fun addDropdown(callback: (dropdown: DropdownComponent) -> Any): Setting
    fun addButton(callback: (button: ButtonComponent) -> Any): Setting
}

external class TextComponent {
    var inputEl: HTMLElement

    fun setValue(value: String): TextComponent
    fun getValue(): String
    fun setPlaceholder(placeholder: String): TextComponent
    fun setDisabled(disabled: Boolean): TextComponent
    fun onChange(callback: (value: String) -> Any): TextComponent
}

external class ToggleComponent {
    var toggleEl: HTMLElement

    fun setValue(value: Boolean): ToggleComponent
    fun getValue(): Boolean
    fun setDisabled(disabled: Boolean): ToggleComponent
    fun onChange(callback: (value: Boolean) -> Any): ToggleComponent
}

external class DropdownComponent {
    var selectEl: HTMLElement

    fun addOption(value: String, display: String): DropdownComponent
    fun addOptions(options: dynamic): DropdownComponent
    fun setValue(value: String): DropdownComponent
    fun getValue(): String
    fun setDisabled(disabled: Boolean): DropdownComponent
    fun onChange(callback: (value: String) -> Any): DropdownComponent
}

external class ButtonComponent {
    var buttonEl: HTMLElement

    fun setButtonText(text: String): ButtonComponent
    fun setIcon(icon: IconName): ButtonComponent
    fun setDisabled(disabled: Boolean): ButtonComponent
    fun setCta(): ButtonComponent
    fun setWarning(): ButtonComponent
    fun onClick(callback: (evt: MouseEvent) -> Any): ButtonComponent
}

external class Notice(message: String, duration: Int = definedExternally) {
    var noticeEl: HTMLElement
    var containerEl: HTMLElement
    var messageEl: HTMLElement

    fun setMessage(message: String): Notice
    fun hide()
}

external class WorkspaceLeaf : Events {
    fun setPinned(isPinned: Boolean)

    /**
     * Open a file in this leaf
     * @param file - The file to open
     */
    fun openFile(file: TFile?): Promise<Unit>

    /**
     * Get the current view state
     */
    fun getViewState(): ViewState

    /**
     * Set the view state
     * @param viewState - The new view state
     * @param eState - Optional ephemeral state
     */
    fun setViewState(viewState: ViewState, eState: Any? = definedExternally): Promise<Unit>

    /**
     * Detach (close) this leaf
     */
    fun detach()
}
