package land.tbp.augment.cli.session.importer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNull.content

// ============================================================================
// Enum serializers for deserializing by integer value
// ============================================================================

@Serializable(with = RequestNodeTypeSerializer::class)
enum class RequestNodeType(val value: Int) {
    Text(0),
    ToolResult(1),
    IdeState(4),
    ;

    companion object {
        private val map = entries.associateBy { it.value }
        fun fromValue(value: Int): RequestNodeType =
            map[value] ?: throw IllegalArgumentException("Unknown RequestNodeType: $value")
    }
}

object RequestNodeTypeSerializer : KSerializer<RequestNodeType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RequestNodeType", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: RequestNodeType) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder): RequestNodeType = RequestNodeType.fromValue(decoder.decodeInt())
}

@Serializable(with = ResponseNodeTypeSerializer::class)
enum class ResponseNodeType(val value: Int) {
    Text(0),
    ToolUse(5),
    Thinking(8),
    TokenUsage(10),
    ;

    companion object {
        private val map = entries.associateBy { it.value }
        fun fromValue(value: Int): ResponseNodeType =
            map[value] ?: throw IllegalArgumentException("Unknown ResponseNodeType: $value")
    }
}

object ResponseNodeTypeSerializer : KSerializer<ResponseNodeType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResponseNodeType", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: ResponseNodeType) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder): ResponseNodeType = ResponseNodeType.fromValue(decoder.decodeInt())
}

// ============================================================================
// Session DTOs
// ============================================================================

@Serializable
data class Session(
    val sessionId: String,
    val created: String,
    val modified: String,
    val chatHistory: List<ChatHistory>,
    val agentState: AgentState,
    val rootTaskUuid: String,
    val customTitle: String? = null,
    val terminalId: String? = null,
) {
    override fun toString(): String {
        return "Session(" +
            "sessionId='$sessionId', " +
            "created='$created', " +
            "modified='$modified', " +
            "chatHistory=$chatHistory, " +
            "agentState=$agentState, " +
            "rootTaskUuid='$rootTaskUuid', " +
            "customTitle=$customTitle, " +
            "terminalId=$terminalId" +
            ")"
    }
}

@Serializable
data class AgentState(
    val userGuidelines: String,
    val workspaceGuidelines: String,
    val agentMemories: String,
    val modelId: String,
    val userEmail: String,
) {
    override fun toString(): String {
        return "AgentState(" +
            "userGuidelines='$userGuidelines', " +
            "workspaceGuidelines='$workspaceGuidelines', " +
            "agentMemories='$agentMemories', " +
            "modelId='$modelId', " +
            "userEmail='$userEmail'" +
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
    @SerialName("request_message") val requestMessage: String,
    @SerialName("response_text") val responseText: String,
    @SerialName("request_id") val requestId: String,
    @SerialName("request_nodes") val requestNodes: List<RequestNode>,
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

@Serializable
data class RequestNode(
    val id: Int,
    val type: RequestNodeType,
    @SerialName("text_node") val textNode: TextNode? = null,
    @SerialName("tool_result_node") val toolResultNode: ToolResultNode? = null,
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

@Serializable
data class ToolResultNode(
    @SerialName("tool_use_id") val toolUseId: String,
    val content: String,
    @SerialName("is_error") val isError: Boolean,
    @SerialName("duration_ms") val durationMs: Long? = null,
    @SerialName("start_time_ms") val startTimeMs: Long? = null,
    @SerialName("request_id") val requestId: String? = null,
    val metadata: ToolResultMetadata? = null,
) {
    override fun toString(): String {
        return "ToolResultNode(" +
            "toolUseId='$toolUseId', " +
            "content='$content', " +
            "isError=$isError, " +
            "durationMs=$durationMs, " +
            "startTimeMs=$startTimeMs, " +
            "requestId=$requestId, " +
            "metadata=$metadata" +
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
    /**
     * - include: ❌
     * - not interesting
     */
    // val id: Int,
    val type: ResponseNodeType,
    val content: String,
    @SerialName("tool_use") val toolUse: ToolUse? = null,
    val thinking: Thinking? = null,
    @SerialName("billing_metadata") val billingMetadata: String? = null,
    val metadata: Metadata? = null,
    @SerialName("token_usage") val tokenUsage: TokenUsage? = null,
    @SerialName("timestamp_ms") val timestampMs: Long? = null,
) {
    override fun toString(): String {
        return "ResponseNode(" +
            "type=$type, " +
            "content='$content', " +
            "toolUse=$toolUse, " +
            "thinking=$thinking, " +
            "billingMetadata=$billingMetadata, " +
            "metadata=$metadata, " +
            "tokenUsage=$tokenUsage, " +
            "timestampMs=$timestampMs" +
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
        return "Thinking(" +
            "summary='$summary', " +
            "content=$content, " +
            ")"
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
