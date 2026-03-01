package land.tbp.augment.cli.session.importer

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import node.buffer.BufferEncoding
import node.buffer.utf8
import node.fs.ReaddirSyncWithFileTypesOptions
import node.fs.readFileSync
import node.fs.readdirSync

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

fun main() {
    val importer = AugmentImporter()
    importer.countKeys()
}

class AugmentImporter {
    // val augmentPath = """C:\Users\TheBestPessimist\.augment"""
    val augmentPath = """C:\Users\TheBestPessimist\.augment\sessions"""
    val keys: MutableMap<String, MutableSet<String>> = mutableMapOf()

    fun countKeys(path: String = augmentPath, prefix: String = "") {
        val entries = readdirSync(path, ReaddirSyncWithFileTypesOptions(withFileTypes = true))
        entries.forEach { dirent ->
            if (dirent.isFile()) {
                val f = readFileSync("""$path/${dirent.name}""", BufferEncoding.utf8)
                println(dirent.name)

                val jsonElement = json.parseToJsonElement(f)
                collectKeys(jsonElement, "", dirent.name.toString())

                val session: Session = json.decodeFromString(f)
                println(session)
            } else {
                error("dirs aren't supported")
            }
        }

        if (prefix.isEmpty()) {
            keys.forEach { (key, files) ->
                println("$key (${files.size})")
            }
        }
    }

    private fun collectKeys(element: JsonElement, path: String, fileName: String) {
        when (element) {
            is JsonObject -> {
                element.forEach { (key, value) ->
                    val currentPath = if (path.isEmpty()) key else "$path.$key"
                    keys.getOrPut(currentPath) { mutableSetOf() }.add(fileName)
                    collectKeys(value, currentPath, fileName)
                }
            }

            is JsonArray -> {
                element.forEachIndexed { index, value ->
                    collectKeys(value, path, fileName)
                }
            }

            is JsonPrimitive -> {
                // Leaf node - no keys to collect
            }
        }
    }
}
