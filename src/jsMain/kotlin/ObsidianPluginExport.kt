// Root-level export file (no package declaration)
// This file exports the plugin at module.exports.default for Obsidian compatibility

import land.tbp.toolbox.TbpToolboxPlugin
import obsidian.App
import obsidian.PluginManifest

/**
 * Root-level export for Obsidian plugin.
 * Obsidian expects module.exports.default to be the plugin class.
 *
 * Since Kotlin/JS @JsExport exports to package namespaces (module.exports.land.tbp.toolbox.default),
 * we create this root-level (no package) class to export at module.exports.default
 *
 */
@OptIn(kotlin.js.ExperimentalJsExport::class)
@JsExport
@JsName("default")
class ObsidianPlugin(app: App, manifest: PluginManifest) : TbpToolboxPlugin(app, manifest)
