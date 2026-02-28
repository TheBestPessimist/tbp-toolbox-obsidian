package land.tbp.node

/**
 * Node.js `fs` module external declarations.
 * @see <a href="https://nodejs.org/api/fs.html">Node.js File System API</a>
 */
@JsModule("fs")
@JsNonModule
external object Fs {
    fun readdirSync(path: String, options: dynamic = definedExternally): Array<Dirent>
    fun readFileSync(path: String, options: dynamic = definedExternally): dynamic
    fun writeFileSync(path: String, data: String, options: dynamic = definedExternally)
    fun statSync(path: String): Stats
    fun existsSync(path: String): Boolean
    fun mkdirSync(path: String, options: dynamic = definedExternally)
    fun rmdirSync(path: String, options: dynamic = definedExternally)
    fun unlinkSync(path: String)
    fun copyFileSync(src: String, dest: String, mode: Int = definedExternally)
    fun renameSync(oldPath: String, newPath: String)
}

external interface Stats {
    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isSymbolicLink(): Boolean
    val size: Double
    val mtime: dynamic
    val ctime: dynamic
    val atime: dynamic
}

external interface Dirent {
    val name: String
    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isSymbolicLink(): Boolean
}

/**
 * Helper function to create JS options object for readdirSync.
 */
fun readdirOptions(withFileTypes: Boolean = false, encoding: String = "utf8"): dynamic {
    val options = js("{}")
    options.withFileTypes = withFileTypes
    options.encoding = encoding
    return options
}

/**
 * Helper function to create JS options object for readFileSync.
 */
fun readFileOptions(encoding: String = "utf8"): dynamic {
    val options = js("{}")
    options.encoding = encoding
    return options
}

/**
 * Helper function to create JS options object for mkdirSync.
 */
fun mkdirOptions(recursive: Boolean = false): dynamic {
    val options = js("{}")
    options.recursive = recursive
    return options
}

