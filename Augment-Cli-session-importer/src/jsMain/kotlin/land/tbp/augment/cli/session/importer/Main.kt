package land.tbp.augment.cli.session.importer

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import node.fs.ReaddirSyncWithFileTypesOptions
import node.fs.readdirSync

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
        val entries = readdirSync(path, ReaddirSyncWithFileTypesOptions(withFileTypes = true))
        entries.forEach { dirent ->
            val isDir = dirent.isDirectory()
            val suffix = if (isDir) """\""" else ""
            println("$prefix${dirent.name}$suffix")

            // Recurse into directories
            if (isDir) {
                val childPath = "$path/${dirent.name}"
                traverseWithNodeFs(childPath, indent + 1)
            }
        }
    }
}
