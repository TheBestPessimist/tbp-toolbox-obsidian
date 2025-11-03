package land.tbp.toolbox.demostuff

import obsidian.App
import obsidian.Notice
import obsidian.TFile
import kotlin.js.Promise



// // Add command to collect and print all ViewState types
// addCommand(command {
//     id = "collect-viewstate-types"
//     name = "Collect and print all ViewState types"
//     callback = {
//         collectAndPrintViewStateTypes()
//     }
// })


/**
 * Collect all ViewState.type values by opening one file per extension and checking its type
 */
fun collectAndPrintViewStateTypes(app: App) {
    val viewStateTypes = mutableSetOf<String>()
    val files = app.vault.getFiles()

    // Filter to get only one file per extension
    val seenExtensions = mutableSetOf<String>()
    val filesToScan = files.filter { file ->
        val extension = file.extension.lowercase()
        if (seenExtensions.contains(extension)) {
            false
        } else {
            seenExtensions.add(extension)
            true
        }
    }

    Notice("Scanning ${filesToScan.size} files (one per extension)... This may take a moment.")
    console.log("=== Starting ViewState.type collection ===")
    console.log("Total files in vault: ${files.size}")
    console.log("Unique extensions found: ${filesToScan.size}")
    console.log("Files to scan: ${filesToScan.size}")

    // Process files sequentially (creating a new leaf for each file)
    processFilesSequentially(filesToScan, 0, viewStateTypes, app)
}


/**
 * Process files one by one to collect ViewState types
 * Creates a fresh leaf for each file to avoid state contamination
 */
fun processFilesSequentially(
    files: List<TFile>,
    index: Int,
    viewStateTypes: MutableSet<String>,
    app: App,
) {
    if (index >= files.size) {
        // Done processing all files

        // Print results
        console.log("")
        console.log("=== All ViewState.type values found in vault ===")
        console.log("Total unique ViewState types: ${viewStateTypes.size}")
        console.log("Total unique extensions scanned: ${files.size}")
        console.log("")
        console.log("ViewState types (sorted):")
        viewStateTypes.sorted().forEach { type ->
            console.log("  - $type")
        }
        console.log("================================================")

        Notice("Found ${viewStateTypes.size} unique ViewState types from ${files.size} unique extensions. Check console for details.")
        return
    }

    val file = files[index]

    // Create a fresh leaf for this file to avoid state contamination
    val tempLeaf = app.workspace.getLeaf(false)

    // Open the file and get its ViewState type
    tempLeaf.openFile(file).then {
        // Add a small delay to ensure the view has fully loaded
        Promise<Unit> { resolve, _ ->
            kotlinx.browser.window.setTimeout(
                {
                    resolve(Unit)
                },
                50,
            )
        }.then {
            val viewState = tempLeaf.getViewState()
            val viewType = viewState.type
            viewStateTypes.add(viewType)

            console.log("Extension: .${file.extension} -> ViewState.type: '$viewType' (file: ${file.path})")

            // Close this leaf before processing the next file
            tempLeaf.detach()

            // Process next file
            processFilesSequentially(files, index + 1, viewStateTypes, app)
        }
    }.catch { error ->
        console.error("Error opening file ${file.path}:", error)
        // Close the leaf and continue with next file even if this one failed
        tempLeaf.detach()
        processFilesSequentially(files, index + 1, viewStateTypes, app)
    }
}
