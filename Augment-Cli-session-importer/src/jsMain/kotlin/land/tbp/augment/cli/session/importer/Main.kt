package land.tbp.augment.cli.session.importer

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import node.buffer.BufferEncoding
import node.buffer.utf8
import js.objects.unsafeJso
import node.fs.ReaddirSyncWithFileTypesOptions
import node.fs.StatSyncFnSimpleThrowIfNoEntryOptions
import node.fs.readFileSync
import node.fs.readdirSync
import node.fs.statSync
import kotlin.collections.component1
import kotlin.collections.component2

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

fun main() {
    // Test with a specific session file
    // val testSessionPath = """C:\Users\TheBestPessimist\.augment\sessions\bcdb2e51-ed3f-4f85-9f92-2e7ccf007d2c.json"""
    val testSessionPath = """C:\Users\TheBestPessimist\.augment\sessions\1e103837-ea1a-4224-bf80-9209d68e8142.json"""

    val fileContent = readFileSync(testSessionPath, BufferEncoding.utf8)
    val session: Session = json.decodeFromString(fileContent)

    // Export to Markdown
    val markdown = session.toMarkdown()
    println(markdown)
}

class AugmentImporter {
    val augmentPath = """C:\Users\TheBestPessimist\.augment\sessions"""
    val keys: MutableMap<String, MutableSet<String>> = mutableMapOf()
    val content = mutableListOf<Session>()

    fun process(path: String = augmentPath, prefix: String = "") {
        // Sort files by creation time (birthtime) descending - newest first
        readdirSync(path, ReaddirSyncWithFileTypesOptions(withFileTypes = true))
            .sortedByDescending { dirent ->
                val fullPath = "$path/${dirent.name}"
                statSync(fullPath, unsafeJso<StatSyncFnSimpleThrowIfNoEntryOptions>()).birthtimeMs
            }.forEach { dirent ->
                if (dirent.isFile()) {
                    val f = readFileSync("""$path/${dirent.name}""", BufferEncoding.utf8)

                    val jsonElement = json.parseToJsonElement(f)
                    collectKeys(jsonElement, "", dirent.name.toString())

                    val session: Session = json.decodeFromString(f)
                    content.add(session)
                    // Note: No need for explicit validation - deserialization will fail
                    // automatically if an invalid enum value is encountered
                } else {
                    error("dirs aren't supported")
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
