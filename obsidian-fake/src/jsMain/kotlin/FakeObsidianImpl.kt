@file:Suppress("unused", "UNUSED_PARAMETER", "RedundantNullableReturnType")

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise

/**
 * Fake implementations of Obsidian API classes for testing.
 * These are minimal stub implementations that allow tests to run.
 *
 * Note: These implementations are exported as the "obsidian" module for testing.
 * The external declarations come from the obsidian-api project.
 */

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Component {
    open fun load() {}
    open fun unload() {}
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class App {
    var workspace: Workspace = Workspace()
    var vault: Vault = Vault()
    var metadataCache: MetadataCache = MetadataCache()
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Workspace {
    var activeLeaf: WorkspaceLeaf? = null
    private val leaves = mutableListOf<WorkspaceLeaf>()

    fun <T> getActiveViewOfType(type: dynamic): T? = null
    fun getActiveFile(): TFile? = null
    fun iterateAllLeaves(callback: (leaf: WorkspaceLeaf) -> Any) {
        leaves.forEach { callback(it) }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Vault {
    private val files = mutableListOf<TFile>()

    fun getFiles(): Array<TFile> = files.toTypedArray()
    fun getMarkdownFiles(): Array<TFile> = files.filter { it.extension == "md" }.toTypedArray()
    fun getAllLoadedFiles(): Array<dynamic> = files.toTypedArray()
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class MetadataCache

@OptIn(ExperimentalJsExport::class)
@JsExport
open class TFile {
    var path: String = ""
    var name: String = ""
    var extension: String = ""
    var basename: String = ""
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Editor {
    private var content: String = ""
    private var selection: String = ""
    
    fun getSelection(): String = selection
    fun replaceSelection(replacement: String) {
        selection = replacement
    }
    fun getValue(): String = content
    fun setValue(content: String) {
        this.content = content
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class PluginManifest(
    var id: String = "",
    var name: String = "",
    var version: String = "",
    var minAppVersion: String = "",
    var description: String = "",
    var author: String = "",
    var authorUrl: String? = null,
    var isDesktopOnly: Boolean? = null
)

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Plugin(val app: App, val manifest: PluginManifest) : Component() {
    open fun onload(): Any = Unit
    open fun onunload() {}
    
    fun addRibbonIcon(icon: String, title: String, callback: (evt: MouseEvent) -> Any?): HTMLElement {
        return document.createElement("div") as HTMLElement
    }
    
    fun addStatusBarItem(): HTMLElement {
        return document.createElement("div") as HTMLElement
    }
    
    fun addCommand(command: dynamic): dynamic {
        return command
    }
    
    fun addSettingTab(tab: PluginSettingTab) {}
    
    fun registerDomEvent(el: dynamic, event: String, callback: (evt: Event) -> Any) {}
    
    fun registerInterval(intervalId: Int): Int = intervalId
    
    fun loadData(): Promise<Any?> {
        return Promise.resolve(null)
    }
    
    fun saveData(data: Any?): Promise<Unit> {
        return Promise.resolve(Unit)
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Modal(val app: App) {
    var contentEl: HTMLElement = document.createElement("div") as HTMLElement
    
    open fun onOpen() {}
    open fun onClose() {}
    fun open() { onOpen() }
    fun close() { onClose() }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class PluginSettingTab(val app: App, val plugin: Plugin) {
    var containerEl: HTMLElement = document.createElement("div") as HTMLElement
    
    open fun display() {}
    open fun hide() {}
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Setting(containerEl: HTMLElement) {
    var settingEl: HTMLElement = document.createElement("div") as HTMLElement
    var infoEl: HTMLElement = document.createElement("div") as HTMLElement
    var nameEl: HTMLElement = document.createElement("div") as HTMLElement
    var descEl: HTMLElement = document.createElement("div") as HTMLElement
    var controlEl: HTMLElement = document.createElement("div") as HTMLElement
    
    fun setName(name: String): Setting = this
    fun setDesc(desc: String): Setting = this
    fun setHeading(): Setting = this
    fun setDisabled(disabled: Boolean): Setting = this
    fun addText(callback: (text: TextComponent) -> Any): Setting {
        callback(TextComponent())
        return this
    }
    fun addToggle(callback: (toggle: ToggleComponent) -> Any): Setting {
        callback(ToggleComponent())
        return this
    }
    fun addDropdown(callback: (dropdown: DropdownComponent) -> Any): Setting {
        callback(DropdownComponent())
        return this
    }
    fun addButton(callback: (button: ButtonComponent) -> Any): Setting {
        callback(ButtonComponent())
        return this
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class TextComponent {
    var inputEl: HTMLElement = document.createElement("input") as HTMLElement
    private var value: String = ""
    
    fun setValue(value: String): TextComponent {
        this.value = value
        return this
    }
    fun getValue(): String = value
    fun setPlaceholder(placeholder: String): TextComponent = this
    fun setDisabled(disabled: Boolean): TextComponent = this
    fun onChange(callback: (value: String) -> Any): TextComponent = this
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class ToggleComponent {
    var toggleEl: HTMLElement = document.createElement("input") as HTMLElement
    private var value: Boolean = false
    
    fun setValue(value: Boolean): ToggleComponent {
        this.value = value
        return this
    }
    fun getValue(): Boolean = value
    fun setDisabled(disabled: Boolean): ToggleComponent = this
    fun onChange(callback: (value: Boolean) -> Any): ToggleComponent = this
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class DropdownComponent {
    var selectEl: HTMLElement = document.createElement("select") as HTMLElement
    private var value: String = ""
    
    fun addOption(value: String, display: String): DropdownComponent = this
    fun addOptions(options: dynamic): DropdownComponent = this
    fun setValue(value: String): DropdownComponent {
        this.value = value
        return this
    }
    fun getValue(): String = value
    fun setDisabled(disabled: Boolean): DropdownComponent = this
    fun onChange(callback: (value: String) -> Any): DropdownComponent = this
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class ButtonComponent {
    var buttonEl: HTMLElement = document.createElement("button") as HTMLElement
    
    fun setButtonText(text: String): ButtonComponent = this
    fun setIcon(icon: String): ButtonComponent = this
    fun setDisabled(disabled: Boolean): ButtonComponent = this
    fun setCta(): ButtonComponent = this
    fun setWarning(): ButtonComponent = this
    fun onClick(callback: (evt: MouseEvent) -> Any): ButtonComponent = this
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class Notice(message: String, duration: Int = 5000) {
    var noticeEl: HTMLElement = document.createElement("div") as HTMLElement
    var containerEl: HTMLElement = document.createElement("div") as HTMLElement
    var messageEl: HTMLElement = document.createElement("div") as HTMLElement
    
    init {
        messageEl.textContent = message
    }
    
    fun setMessage(message: String): Notice {
        messageEl.textContent = message
        return this
    }
    fun hide() {}
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class MarkdownView {
    var app: App = App()
    var file: TFile? = null
    var editor: Editor = Editor()
    var leaf: WorkspaceLeaf = WorkspaceLeaf()
}
@OptIn(ExperimentalJsExport::class)
@JsExport
open class ViewState {
    var type: String = "markdown"
    var state: dynamic = null
    var active: Boolean? = null
    var pinned: Boolean? = null
}

@OptIn(ExperimentalJsExport::class)
@JsExport
open class WorkspaceLeaf {
    private var viewState: ViewState = ViewState()

    fun getViewState(): ViewState {
        return viewState
    }

    fun setViewState(viewState: ViewState, eState: Any? = null): Promise<Unit> {
        this.viewState = viewState
        return Promise.resolve(Unit)
    }
}
