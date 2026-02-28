package land.tbp.augment.cli.session.importer

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun main() {
    val importer = AugmentImporter()

    println("=== Using kotlinx.io ===")
    importer.listEverythingWithKotlinxIo()

    println("\n=== Using Node.js fs ===")
    importer.listEverythingWithNodeFs()
}

class AugmentImporter {
    val augmentPath = """C:\Users\TheBestPessimist\.augment"""

    /**
     * List all files and directories recursively using kotlinx.io SystemFileSystem
     */
    fun listEverythingWithKotlinxIo() {
        val p = Path(augmentPath)
        println("Root: $p")
        traverseWithKotlinxIo(p, indent = 0)
    }

    private fun traverseWithKotlinxIo(path: Path, indent: Int) {
        val prefix = "    ".repeat(indent)
        SystemFileSystem.list(path).forEach { childPath ->
            val metadata = SystemFileSystem.metadataOrNull(childPath)
            val isDir = metadata?.isDirectory == true
            val suffix = if (isDir) "\\" else ""
            println("$prefix${childPath.name}$suffix")

            // Recurse into directories
            if (isDir) {
                traverseWithKotlinxIo(childPath, indent + 1)
            }
        }
    }

    /**
     * List all files and directories recursively using Node.js fs module
     */
    fun listEverythingWithNodeFs() {
        println("Root: $augmentPath")
        traverseWithNodeFs(augmentPath, indent = 0)
    }

    private fun traverseWithNodeFs(path: String, indent: Int) {
        val prefix = "    ".repeat(indent)
        val entries = NodeFs.readdirSync(path, jsReaddirOptions(withFileTypes = true))
        entries.forEach { dirent ->
            val isDir = dirent.isDirectory()
            val suffix = if (isDir) "\\" else ""
            println("$prefix${dirent.name}$suffix")

            // Recurse into directories
            if (isDir) {
                val childPath = "$path/${dirent.name}"
                traverseWithNodeFs(childPath, indent + 1)
            }
        }
    }
}

// Node.js fs module external declarations
@JsModule("fs")
@JsNonModule
external object NodeFs {
    fun readdirSync(path: String, options: dynamic = definedExternally): Array<Dirent>
    fun statSync(path: String): Stats
    fun existsSync(path: String): Boolean
}

external interface Stats {
    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isSymbolicLink(): Boolean
}

external interface Dirent {
    val name: String
    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isSymbolicLink(): Boolean
}

// Helper function to create JS options object
fun jsReaddirOptions(withFileTypes: Boolean = false, encoding: String = "utf8"): dynamic {
    val options = js("{}")
    options.withFileTypes = withFileTypes
    options.encoding = encoding
    return options
}
