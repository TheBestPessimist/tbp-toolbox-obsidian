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
import kotlin.collections.component1
import kotlin.collections.component2

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

fun main() {

    /*
    classes to check
        ✅ Session
        ✅ AgentState
        ChatHistory
        Exchange
        RequestNode
        TextNode
        ToolResultNode
        ToolResultMetadata
        IdeStateNode
        WorkspaceFolder
        CurrentTerminal
        ✅ ResponseNode
        ToolUse
        ✅ Thinking
        TokenUsage
        Metadata
     */

    val importer = AugmentImporter()
    importer.process()

    // importer.keys.forEach { (key, files) ->
    //     println("$key (${files.size})")
    // }

    importer.content.forEach {
        it.chatHistory.forEach {

        println()
        println("========")

            it.completed




        //     it.exchange.responseNodes.forEach {
        //
        //         // it.content
        //         //
        //         println()
        //         println("========")
        //         // println("""${it.type}${it.content}""")
        //
        //     }
        }
    }
}

class AugmentImporter {
    val augmentPath = """C:\Users\TheBestPessimist\.augment\sessions"""
    val keys: MutableMap<String, MutableSet<String>> = mutableMapOf()
    val content = mutableListOf<Session>()

    fun process(path: String = augmentPath, prefix: String = "") {
        val entries = readdirSync(path, ReaddirSyncWithFileTypesOptions(withFileTypes = true))
        entries.forEach { dirent ->
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
