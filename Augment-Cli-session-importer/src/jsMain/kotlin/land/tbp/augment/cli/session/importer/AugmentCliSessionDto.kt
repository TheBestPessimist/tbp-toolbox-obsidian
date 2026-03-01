package land.tbp.augment.cli.session.importer

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Session(
    val sessionId: String,
    val created: String,
    val modified: String,
    val chatHistory: List<ChatHistory>,
    val agentState: AgentState,
    val rootTaskUuid: String,
    val customTitle: String,
)

@Serializable
data class AgentState(
    val userGuidelines: String,
    val workspaceGuidelines: String,
    val agentMemories: String,
    val modelId: String,
    val userEmail: String,
)

@Serializable
data class ChatHistory(
    val exchange: Exchange,
    val completed: Boolean,
    val sequenceId: Int,
    val finishedAt: String,
    val changedFiles: List<String>,
    val changedFilesSkipped: List<String>,
    val changedFilesSkippedCount: Int,
    val isHistorySummary: Boolean,
    val historySummaryVersion: Int,
    val source: String,
)

@Serializable
data class Exchange(
    @SerialName("request_message") val requestMessage: String,
    @SerialName("response_text") val responseText: String,
    @SerialName("request_id") val requestId: String,
    @SerialName("request_nodes") val requestNodes: List<RequestNodes>,
    @SerialName("response_nodes") val responseNodes: List<ResponseNodes>,
)

@Serializable
data class RequestNodes(
    val id: Int,
    val type: Int,
    @SerialName("text_node") val textNode: TextNode?,
)

@Serializable
data class ResponseNodes(
    val id: Int,
    val type: Int,
    val content: String,
    @SerialName("tool_use") val toolUse: String? = null,
    val thinking: Thinking,
    @SerialName("billing_metadata") val billingMetadata: String? = null,
    val metadata: Metadata,
    @SerialName("token_usage") val tokenUsage: String? = null,
    @SerialName("timestamp_ms") val timestampMs: Long,
)

@Serializable
data class TextNode(
    val content: String,
)

@Serializable
data class Thinking(
    val summary: String,
    @SerialName("encrypted_content") val encryptedContent: String,
    val content: String? = null,
    @SerialName("openai_responses_api_item_id") val openaiResponsesApiItemId: String? = null,
)

@Serializable
data class Metadata(
    @SerialName("openai_id") val openaiId: String? = null,
    @SerialName("google_ts") val googleTs: String? = null,
    val provider: String,
    val phase: String? = null,
)
