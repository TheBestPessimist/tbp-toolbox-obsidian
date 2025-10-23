package land.tbp.toolbox

import obsidian.*
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise
import kotlin.js.unsafeCast

/**
 * Settings interface for the plugin1
 */
interface MyPluginSettings {
	var mySetting: String
}

/**
 * Default settings implementation
 */
class DefaultSettings : MyPluginSettings {
	override var mySetting: String = "default"
}

/**
 * Helper to create Command objects in pure Kotlin
 */
inline fun command(block: CommandBuilder.() -> Unit): Command {
	return CommandBuilder().apply(block).build()
}

class CommandBuilder {
	var id: String = ""
	var name: String = ""
	var icon: IconName? = null
	var mobileOnly: Boolean? = null
	var repeatable: Boolean? = null
	var callback: (() -> Any?)? = null
	var checkCallback: ((checking: Boolean) -> Any)? = null
	var editorCallback: ((editor: Editor, ctx: Any) -> Any?)? = null
	var editorCheckCallback: ((checking: Boolean, editor: Editor, ctx: Any) -> Any)? = null
	var hotkeys: Array<Hotkey>? = null

	fun build(): Command {
		val cmd = object : Command {
			override var id: String = this@CommandBuilder.id
			override var name: String = this@CommandBuilder.name
			override var icon: IconName? = this@CommandBuilder.icon
			override var mobileOnly: Boolean? = this@CommandBuilder.mobileOnly
			override var repeatable: Boolean? = this@CommandBuilder.repeatable
			override var callback: (() -> Any?)? = this@CommandBuilder.callback
			override var checkCallback: ((checking: Boolean) -> Any)? = this@CommandBuilder.checkCallback
			override var editorCallback: ((editor: Editor, ctx: Any) -> Any?)? = this@CommandBuilder.editorCallback
			override var editorCheckCallback: ((checking: Boolean, editor: Editor, ctx: Any) -> Any)? = this@CommandBuilder.editorCheckCallback
			override var hotkeys: Array<Hotkey>? = this@CommandBuilder.hotkeys
		}
		return cmd.unsafeCast<Command>()
	}
}

/**
 * Main plugin class
 * This will be exported as the default export for Obsidian to load
 */
@JsExport
@JsName("default")
class MyPlugin(app: App, manifest: PluginManifest) : Plugin(app, manifest) {
	var settings: MyPluginSettings = DefaultSettings()

	override fun onload(): Any {
		console.log("Loading MyPlugin in Kotlin!")

		// Return a promise for async loading
		return Promise<Unit> { resolve, reject ->
			try {
				// Load settings first
				loadSettings().then {
					setupPlugin()
					resolve(Unit)
				}.catch { error ->
					console.error("Error loading settings:", error)
					reject(error)
				}
			} catch (e: Throwable) {
				console.error("Error in onload:", e)
				reject(e)
			}
		}
	}

	private fun setupPlugin() {
		// Add ribbon icon
		val ribbonIconEl = addRibbonIcon("dice", "Sample Plugin") { _evt: MouseEvent ->
			Notice("This is a notice!")
		}
		// Add CSS class to ribbon icon
		ribbonIconEl.addClass("my-plugin-ribbon-class")

		// Add status bar item
		val statusBarItemEl = addStatusBarItem()
		statusBarItemEl.setText("Status Bar Text")

		// Create modal instance to use in commands
		val sampleModal = SampleModal(app)

		// Add simple command - pure Kotlin
		addCommand(command {
			id = "open-sample-modal-simple"
			name = "Open sample modal (simple)"
			callback = {
				sampleModal.open()
			}
		})

		// Add editor command - pure Kotlin
		addCommand(command {
			id = "sample-editor-command"
			name = "Sample editor command"
			editorCallback = { editor, _view ->
				console.log(editor.getSelection())
				editor.replaceSelection("Sample Editor Command")
			}
		})

		// Add complex command with check callback - pure Kotlin
		addCommand(command {
			id = "open-sample-modal-complex"
			name = "Open sample modal (complex)"
			checkCallback = { checking ->
				// Get the active MarkdownView using the proper type constructor
				val markdownView = app.workspace.getActiveViewOfType(MarkdownView::class.js.unsafeCast<Constructor<MarkdownView>>())
				if (markdownView != null) {
					if (!checking) {
						sampleModal.open()
					}
					true
				} else {
					false
				}
			}
		})

		// Add settings tab
		addSettingTab(SampleSettingTab(app, this))

		// Register DOM event
		registerDomEvent(kotlinx.browser.document, "click") { evt ->
			console.log("click", evt)
		}

		// Register interval
		val intervalId = kotlinx.browser.window.setInterval({
			console.log("setInterval")
		}, 5 * 60 * 1000)
		registerInterval(intervalId)
	}

	override fun onunload() {
		console.log("Unloading MyPlugin")
	}

	private fun loadSettings(): Promise<Unit> {
		return loadData().then { data ->
			if (data != null) {
				// Merge loaded data with default settings
				val defaultSettings = DefaultSettings()
				settings = mergeSettings(defaultSettings, data.unsafeCast<MyPluginSettings>())
			}
			Unit
		}
	}

	private fun mergeSettings(defaults: MyPluginSettings, loaded: MyPluginSettings): MyPluginSettings {
		return object : MyPluginSettings {
			override var mySetting: String = loaded.mySetting.takeIf { it.isNotEmpty() } ?: defaults.mySetting
		}
	}

	fun saveSettings(): Promise<Unit> {
		return saveData(settings).then {
			Unit
		}
	}
}

/**
 * Sample Modal dialog - pure Kotlin implementation
 */
class SampleModal(app: App) : Modal(app) {
	override fun onOpen() {
		contentEl.setText("Woah!")
	}

	override fun onClose() {
		contentEl.empty()
	}
}

/**
 * Sample Settings Tab - pure Kotlin implementation
 */
class SampleSettingTab(app: App, val myPlugin: MyPlugin) : PluginSettingTab(app, myPlugin) {

	override fun display() {
		containerEl.empty()

		Setting(containerEl)
			.setName("Setting #1")
			.setDesc("It's a secret")
			.addText { text ->
				text
					.setPlaceholder("Enter your secret")
					.setValue(myPlugin.settings.mySetting)
					.onChange { value ->
						myPlugin.settings.mySetting = value
						myPlugin.saveSettings()
					}
			}
	}
}
