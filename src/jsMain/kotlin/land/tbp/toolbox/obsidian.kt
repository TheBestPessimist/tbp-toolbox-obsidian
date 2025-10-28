// Minimal Obsidian API definitions for Kotlin/JS
// This file contains only the types needed for the sample plugin

@file:JsModule("obsidian")

package obsidian

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise

/**
 * Type alias for icon names
 * @public
 */
typealias IconName = String

/**
 * Constructor type for creating instances
 * @public
 */
external interface Constructor<T>

/**
 * @public
 */
external interface PluginManifest {
    var id: String
    var name: String
    var version: String
    var minAppVersion: String
    var description: String
    var author: String
    var authorUrl: String?
    var isDesktopOnly: Boolean?
}

/**
 * @public
 */
external class App {
    var workspace: Workspace
    var vault: Vault
    var metadataCache: MetadataCache
}

/**
 * @public
 */
external class Workspace {
    fun <T> getActiveViewOfType(type: Constructor<T>): T?
    fun getActiveFile(): TFile?
}

/**
 * @public
 */
external class Vault

/**
 * @public
 */
external class MetadataCache

/**
 * @public
 */
external class TFile {
    var path: String
    var name: String
}

/**
 * @public
 */
external interface Hotkey {
    var modifiers: Array<String>
    var key: String
}

/**
 * @public
 */
external interface Command {
    var id: String
    var name: String
    var icon: IconName?
    var mobileOnly: Boolean?
    var repeatable: Boolean?
    var callback: (() -> Any?)?
    var checkCallback: ((checking: Boolean) -> Any)?
    var editorCallback: ((editor: Editor, ctx: Any) -> Any?)?
    var editorCheckCallback: ((checking: Boolean, editor: Editor, ctx: Any) -> Any)?
    var hotkeys: Array<Hotkey>?
}

/**
 * @public
 */
external class Editor {
    fun getSelection(): String
    fun replaceSelection(replacement: String)
    fun getValue(): String
    fun setValue(content: String)
}

/**
 * @public
 */
open external class Component {
    fun load()
    fun unload()
}

/**
 * @public
 */
open external class Plugin(app: App, manifest: PluginManifest) : Component {
    var app: App
    var manifest: PluginManifest
    
    open fun onload(): Any
    open fun onunload()
    
    fun addRibbonIcon(icon: IconName, title: String, callback: (evt: MouseEvent) -> Any?): HTMLElement
    fun addStatusBarItem(): HTMLElement
    fun addCommand(command: Command): Command
    fun addSettingTab(tab: PluginSettingTab)
    fun registerDomEvent(el: dynamic, event: String, callback: (evt: Event) -> Any)
    fun registerInterval(intervalId: Int): Int
    fun loadData(): Promise<Any?>
    fun saveData(data: Any?): Promise<Unit>
}

/**
 * @public
 */
open external class Modal(app: App) {
    var app: App
    var contentEl: HTMLElement
    
    open fun onOpen()
    open fun onClose()
    fun open()
    fun close()
}

/**
 * @public
 */
open external class PluginSettingTab(app: App, plugin: Plugin) {
    var app: App
    var plugin: Plugin
    var containerEl: HTMLElement
    
    open fun display()
    open fun hide()
}

/**
 * @public
 */
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

/**
 * @public
 */
external class TextComponent {
    var inputEl: HTMLElement
    
    fun setValue(value: String): TextComponent
    fun getValue(): String
    fun setPlaceholder(placeholder: String): TextComponent
    fun setDisabled(disabled: Boolean): TextComponent
    fun onChange(callback: (value: String) -> Any): TextComponent
}

/**
 * @public
 */
external class ToggleComponent {
    var toggleEl: HTMLElement
    
    fun setValue(value: Boolean): ToggleComponent
    fun getValue(): Boolean
    fun setDisabled(disabled: Boolean): ToggleComponent
    fun onChange(callback: (value: Boolean) -> Any): ToggleComponent
}

/**
 * @public
 */
external class DropdownComponent {
    var selectEl: HTMLElement
    
    fun addOption(value: String, display: String): DropdownComponent
    fun addOptions(options: dynamic): DropdownComponent
    fun setValue(value: String): DropdownComponent
    fun getValue(): String
    fun setDisabled(disabled: Boolean): DropdownComponent
    fun onChange(callback: (value: String) -> Any): DropdownComponent
}

/**
 * @public
 */
external class ButtonComponent {
    var buttonEl: HTMLElement
    
    fun setButtonText(text: String): ButtonComponent
    fun setIcon(icon: IconName): ButtonComponent
    fun setDisabled(disabled: Boolean): ButtonComponent
    fun setCta(): ButtonComponent
    fun setWarning(): ButtonComponent
    fun onClick(callback: (evt: MouseEvent) -> Any): ButtonComponent
}

/**
 * @public
 */
external class Notice(message: String, duration: Int = definedExternally) {
    var noticeEl: HTMLElement
    var containerEl: HTMLElement
    var messageEl: HTMLElement
    
    fun setMessage(message: String): Notice
    fun hide()
}

/**
 * @public
 */
external class MarkdownView {
    var app: App
    var file: TFile?
    var editor: Editor
}
