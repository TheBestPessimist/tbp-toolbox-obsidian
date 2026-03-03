package land.tbp.augment.cli.session.importer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * # Augment CLI Session Data Structure
 *
 * ## Understanding the Conversation Flow
 *
 * Each `ChatHistory` entry represents one exchange (request-response cycle).
 *
 * ### Duplication Pattern Explanation:
 *
 * The LLM tool-use loop works like this:
 * 1. User sends message → AI responds with tool calls (`response_nodes` with `type=5`)
 * 2. Tools execute → Results are fed back to AI in the NEXT exchange's `request_nodes` (as `type=1`)
 *
 * This means **tool results appear twice**:
 * - First: implicitly after `ToolUse` in `response_nodes` (the tool was called)
 * - Second: explicitly in the next exchange's `request_nodes` as `ToolResultNode` (fed back to AI)
 *
 * ### What to use for a clean transcript:
 *
 * **From `request_nodes`:**
 * - `TextNode` (type=0): ✅ User's message - IMPORTANT
 * - `ToolResultNode` (type=1): ⚠️ DUPLICATED - skip for transcript, or pair with previous exchange's ToolUse
 * - `IdeStateNode` (type=4): ❌ Just workspace context - not interesting for reading
 *
 * **From `response_nodes`:**
 * - `Text` (type=0): ✅ AI's text output
 * - `ToolUse` (type=5): ✅ Shows what tools AI called - valuable for understanding AI reasoning
 * - `Thinking` (type=8): ✅ AI's reasoning summary - very valuable
 * - `TokenUsage` (type=10): ⚠️ Useful for stats, not for reading
 *
 * ### Recommended approach for transcripts:
 *
 * For each exchange, show:
 * 1. User message (`requestMessage` or `TextNode`)
 * 2. AI thinking summary (from `Thinking`)
 * 3. Tool calls made (from `ToolUse` - just tool names + input)
 * 4. AI response text (`responseText`)
 * 5. Optionally: tool results paired with their ToolUse (from next exchange's `ToolResultNode`)
 */

@Serializable(with = RequestNodeTypeSerializer::class)
enum class RequestNodeType(val value: Int) {
    /** User's text message */
    Text(0),
    /** Tool result from previous exchange - DUPLICATED content, fed back to AI */
    ToolResult(1),
    /** IDE/workspace state - not interesting for transcripts */
    IdeState(4),
}

object RequestNodeTypeSerializer : KSerializer<RequestNodeType> {
    override val descriptor = PrimitiveSerialDescriptor("RequestNodeType", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: RequestNodeType) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder): RequestNodeType {
        val intValue = decoder.decodeInt()
        return RequestNodeType.entries.find { it.value == intValue }
            ?: throw IllegalArgumentException("Unknown RequestNodeType: $intValue")
    }
}

@Serializable(with = ResponseNodeTypeSerializer::class)
enum class ResponseNodeType(val value: Int) {
    /** AI's text output - ✅ include in transcript */
    Text(0),
    /** Tool call made by AI - ✅ include (shows AI reasoning) */
    ToolUse(5),
    /** AI's thinking/reasoning summary - ✅ very valuable */
    Thinking(8),
    /** Token usage statistics - ⚠️ useful for stats only */
    TokenUsage(10),
}

object ResponseNodeTypeSerializer : KSerializer<ResponseNodeType> {
    override val descriptor = PrimitiveSerialDescriptor("ResponseNodeType", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: ResponseNodeType) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder): ResponseNodeType {
        val intValue = decoder.decodeInt()
        return ResponseNodeType.entries.find { it.value == intValue }
            ?: throw IllegalArgumentException("Unknown ResponseNodeType: $intValue")
    }
}

@Serializable
data class Session(
    /**
     * - include ✅
     */
    val sessionId: String,
    /**
     * - include ✅
     */
    val created: String,
    /**
     * - include ✅
     */
    val modified: String,
    val chatHistory: List<ChatHistory>,
    // /**
    //  * - Include: ❌
    //  * - Doesnt have that interesting data as of 2026-03-02
    //  */
    // val agentState: AgentState,
    // /**
    //  * - Include: ❌
    //  */
    // val rootTaskUuid: String,
    /**
     * - include ✅
     */
    val customTitle: String? = null,
    // /**
    //  * - Include: ❌
    //  */
    //  val terminalId: String? = null,
) {
    override fun toString(): String {
        return "Session(" +
            "sessionId='$sessionId', " +
            "created='$created', " +
            "modified='$modified', " +
            "chatHistory=$chatHistory, " +
            "customTitle=$customTitle, " +
            ")"
    }
}

@Serializable
data class ChatHistory(
    val exchange: Exchange,
    val completed: Boolean,
    val sequenceId: Double, // Can be fractional (e.g., 5.5) in some sessions
    val finishedAt: String,
    val changedFiles: List<String>,
    val changedFilesSkipped: List<String>,
    val changedFilesSkippedCount: Int,
    val isHistorySummary: Boolean? = null,
    val historySummaryVersion: Int? = null,
    val source: String? = null,
) {
    override fun toString(): String {
        return "ChatHistory(" +
            "exchange=$exchange, " +
            "completed=$completed, " +
            "sequenceId=$sequenceId, " +
            "finishedAt='$finishedAt', " +
            "changedFiles=$changedFiles, " +
            "changedFilesSkipped=$changedFilesSkipped, " +
            "changedFilesSkippedCount=$changedFilesSkippedCount, " +
            "isHistorySummary=$isHistorySummary, " +
            "historySummaryVersion=$historySummaryVersion, " +
            "source=$source" +
            ")"
    }
}

@Serializable
data class Exchange(
    /**
     * ✅ The user's message text - primary source for user input
     */
    @SerialName("request_message") val requestMessage: String,
    /**
     * ✅ The AI's final response text - primary source for AI output
     */
    @SerialName("response_text") val responseText: String,
    @SerialName("request_id") val requestId: String,
    /**
     * Contains: TextNode (user message), ToolResultNode (DUPLICATED from prev exchange), IdeStateNode (workspace info)
     * For transcripts, you typically only need `requestMessage` instead of parsing these.
     */
    @SerialName("request_nodes") val requestNodes: List<RequestNode>,
    /**
     * Contains: Text (AI output), ToolUse (tool calls), Thinking (reasoning), TokenUsage (stats)
     * All valuable for understanding the AI's response.
     */
    @SerialName("response_nodes") val responseNodes: List<ResponseNode>,
) {
    override fun toString(): String {
        return "Exchange(" +
            "requestMessage='$requestMessage', " +
            "responseText='$responseText', " +
            "requestId='$requestId', " +
            "requestNodes=$requestNodes, " +
            "responseNodes=$responseNodes" +
            ")"
    }
}

/**
 * Represents a node in the request (user's turn).
 *
 * ## Important: Duplication of ToolResultNode
 *
 * `toolResultNode` contains results from tools called in the PREVIOUS exchange.
 * This is how the LLM tool-use loop works:
 * 1. Exchange N: AI calls tool X (via ToolUse in response_nodes)
 * 2. Exchange N+1: Tool X's result appears here as ToolResultNode
 *
 * For clean transcripts, you can either:
 * - Skip `toolResultNode` entirely (the tool call in prev exchange implies the result)
 * - Or pair each `ToolResultNode` with its matching `ToolUse` from the previous exchange
 *   (match via `toolUseId`)
 */
@Serializable
data class RequestNode(
    val id: Int,
    val type: RequestNodeType,
    /** ✅ User's text message - include in transcript */
    @SerialName("text_node") val textNode: TextNode? = null,
    /**
     * ⚠️ DUPLICATED: Tool result from PREVIOUS exchange's tool call.
     * The content here was produced by the ToolUse in the previous response_nodes.
     * Skip for transcript to avoid duplication, or pair with matching ToolUse.toolUseId
     */
    @SerialName("tool_result_node") val toolResultNode: ToolResultNode? = null,
    /** ❌ Workspace/IDE state - not interesting for reading transcripts */
    @SerialName("ide_state_node") val ideStateNode: IdeStateNode? = null,
) {
    override fun toString(): String {
        return "RequestNode(" +
            "id=$id, " +
            "type=$type, " +
            "textNode=$textNode, " +
            "toolResultNode=$toolResultNode, " +
            "ideStateNode=$ideStateNode" +
            ")"
    }
}

@Serializable
data class TextNode(
    val content: String,
) {
    override fun toString(): String {
        return "TextNode(" +
            "content='$content'" +
            ")"
    }
}

/**
 * Result of a tool execution.
 *
 * ⚠️ DUPLICATION NOTE: This appears in `request_nodes` of exchange N+1,
 * representing the result of a `ToolUse` from exchange N's `response_nodes`.
 *
 * To pair with the original tool call, match `toolUseId` with `ToolUse.toolUseId`
 * from the previous exchange.
 *
 * For transcripts: Consider skipping this content since:
 * - It's often very large (file contents, search results)
 * - The `ToolUse` in the previous exchange already shows what was requested
 * - Or show a truncated/summarized version
 */
@Serializable
data class ToolResultNode(
    /** Matches ToolUse.toolUseId from the previous exchange */
    @SerialName("tool_use_id") val toolUseId: String,
    /** The actual result content - can be very large (file contents, etc.) */
    val content: String,
    /** Whether the tool execution resulted in an error */
    @SerialName("is_error") val isError: Boolean,
    /** How long the tool took to execute */
    @SerialName("duration_ms") val durationMs: Long? = null,
    @SerialName("start_time_ms") val startTimeMs: Long? = null,
    @SerialName("request_id") val requestId: String? = null,
    val metadata: ToolResultMetadata? = null,
) {
    override fun toString(): String {
        return "ToolResultNode(" +
            "toolUseId='$toolUseId', " +
            "content='${content.take(100)}${if (content.length > 100) "..." else ""}', " +
            "isError=$isError, " +
            "durationMs=$durationMs" +
            ")"
    }
}

@Serializable
data class ToolResultMetadata(
    @SerialName("tool_lines_added") val toolLinesAdded: Int? = null,
    @SerialName("tool_lines_deleted") val toolLinesDeleted: Int? = null,
) {
    override fun toString(): String {
        return "ToolResultMetadata(" +
            "toolLinesAdded=$toolLinesAdded, " +
            "toolLinesDeleted=$toolLinesDeleted" +
            ")"
    }
}

@Serializable
data class IdeStateNode(
    @SerialName("workspace_folders") val workspaceFolders: List<WorkspaceFolder>,
    @SerialName("workspace_folders_unchanged") val workspaceFoldersUnchanged: Boolean,
    @SerialName("current_terminal") val currentTerminal: CurrentTerminal,
) {
    override fun toString(): String {
        return "IdeStateNode(" +
            "workspaceFolders=$workspaceFolders, " +
            "workspaceFoldersUnchanged=$workspaceFoldersUnchanged, " +
            "currentTerminal=$currentTerminal" +
            ")"
    }
}

@Serializable
data class WorkspaceFolder(
    @SerialName("repository_root") val repositoryRoot: String,
    @SerialName("folder_root") val folderRoot: String,
) {
    override fun toString(): String {
        return "WorkspaceFolder(" +
            "repositoryRoot='$repositoryRoot', " +
            "folderRoot='$folderRoot'" +
            ")"
    }
}

@Serializable
data class CurrentTerminal(
    @SerialName("terminal_id") val terminalId: Int,
    @SerialName("current_working_directory") val currentWorkingDirectory: String,
) {
    override fun toString(): String {
        return "CurrentTerminal(" +
            "terminalId=$terminalId, " +
            "currentWorkingDirectory='$currentWorkingDirectory'" +
            ")"
    }
}

@Serializable
data class ResponseNode(
    // /**
    //  * - include: ❌
    //  * - not interesting
    //  */
    // val id: Int,
    val type: ResponseNodeType,
    val content: String,
    @SerialName("tool_use") val toolUse: ToolUse? = null,
    val thinking: Thinking? = null,
    // /**
    //  * - include :❌
    //  * - this is always null
    //  */
    // @SerialName("billing_metadata") val billingMetadata: String? = null,
    // /**
    //  * - include :❌
    //  * - nothing interesting
    //  */
    // val metadata: Metadata? = null,
    /**
     * Might be interesting
     */
    @SerialName("token_usage") val tokenUsage: TokenUsage? = null,
    // /**
    //  * - include :❌
    //  * - nothing interesting
    //  */
    // @SerialName("timestamp_ms") val timestampMs: Long? = null,
) {
    override fun toString(): String {
        return "ResponseNode(" +
            "type=$type, " +
            "content='$content', " +
            "toolUse=$toolUse, " +
            "thinking=$thinking, " +
            "tokenUsage=$tokenUsage, " +
            ")"
    }
}

@Serializable
data class ToolUse(
    @SerialName("tool_use_id") val toolUseId: String,
    @SerialName("tool_name") val toolName: String,
    @SerialName("input_json") val inputJson: String,
    @SerialName("is_partial") val isPartial: Boolean,
    @SerialName("started_at_ms") val startedAtMs: Long? = null,
    @SerialName("completed_at_ms") val completedAtMs: Long? = null,
) {
    override fun toString(): String {
        return "ToolUse(" +
            "toolUseId='$toolUseId', " +
            "toolName='$toolName', " +
            "inputJson='$inputJson', " +
            "isPartial=$isPartial, " +
            "startedAtMs=$startedAtMs, " +
            "completedAtMs=$completedAtMs" +
            ")"
    }
}

@Serializable
data class Thinking(
    val summary: String,
    // /**
    //  * -include: ❌
    //  * - seems useless
    //  */
    // @SerialName("encrypted_content") val encryptedContent: String,
    // /**
    //  * - include ❌
    //  * - This one seems to be always empty
    //  */
    // val content: String? = null,
    // /**
    //  * - include ❌
    //  * - This one seems to be always empty
    //  */
    // @SerialName("openai_responses_api_item_id") val openaiResponsesApiItemId: String? = null,
) {
    override fun toString(): String {
        return "Thinking(summary='$summary')"
    }
}

@Serializable
data class TokenUsage(
    @SerialName("input_tokens") val inputTokens: Long,
    @SerialName("output_tokens") val outputTokens: Long,
    @SerialName("cache_read_input_tokens") val cacheReadInputTokens: Long,
    @SerialName("cache_creation_input_tokens") val cacheCreationInputTokens: Long,
    @SerialName("system_prompt_tokens") val systemPromptTokens: Long,
    @SerialName("chat_history_tokens") val chatHistoryTokens: Long,
    @SerialName("current_message_tokens") val currentMessageTokens: Long,
    @SerialName("max_context_tokens") val maxContextTokens: Long,
    @SerialName("tool_definitions_tokens") val toolDefinitionsTokens: Long? = null,
    @SerialName("tool_result_tokens") val toolResultTokens: Long? = null,
    @SerialName("assistant_response_tokens") val assistantResponseTokens: Long? = null,
) {
    override fun toString(): String {
        return "TokenUsage(" +
            "inputTokens=$inputTokens, " +
            "outputTokens=$outputTokens, " +
            "cacheReadInputTokens=$cacheReadInputTokens, " +
            "cacheCreationInputTokens=$cacheCreationInputTokens, " +
            "systemPromptTokens=$systemPromptTokens, " +
            "chatHistoryTokens=$chatHistoryTokens, " +
            "currentMessageTokens=$currentMessageTokens, " +
            "maxContextTokens=$maxContextTokens, " +
            "toolDefinitionsTokens=$toolDefinitionsTokens, " +
            "toolResultTokens=$toolResultTokens, " +
            "assistantResponseTokens=$assistantResponseTokens" +
            ")"
    }
}

@Serializable
data class Metadata(
    @SerialName("openai_id") val openaiId: String? = null,
    @SerialName("google_ts") val googleTs: String? = null,
    val provider: String? = null,
    val phase: String? = null,
) {
    override fun toString(): String {
        return "Metadata(" +
            "openaiId=$openaiId, " +
            "googleTs=$googleTs, " +
            "provider=$provider, " +
            "phase=$phase" +
            ")"
    }
}
