package land.tbp.augment.cli.session.importer

/**
 * Session export utilities.
 *
 * This file contains all the logic for extracting clean, de-duplicated data
 * from Augment CLI session files for transcript generation.
 *
 * Kept separate from DTOs to make it easier to extend and test when the
 * JSON structure changes in the future.
 */

// =============================================================================
// Extension functions for extracting data from DTOs
// =============================================================================

/** Get the AI's thinking/reasoning summary from an exchange, if present */
fun Exchange.getThinkingSummary(): String? =
    responseNodes
        .firstOrNull { it.type == ResponseNodeType.Thinking }
        ?.thinking?.summary

/** Get all completed (non-partial) tool calls made by the AI in an exchange */
fun Exchange.getToolCalls(): List<ToolUse> =
    responseNodes
        .filter { it.type == ResponseNodeType.ToolUse }
        .mapNotNull { it.toolUse }
        .filter { !it.isPartial }

/** Get token usage for an exchange, if present */
fun Exchange.getTokenUsage(): TokenUsage? =
    responseNodes
        .firstOrNull { it.type == ResponseNodeType.TokenUsage }
        ?.tokenUsage

// =============================================================================
// Helper data classes for clean export
// =============================================================================

/**
 * A tool call paired with its result (from the next exchange).
 * This is the de-duplicated view of tool usage.
 */
data class ToolCallWithResult(
    val toolUse: ToolUse,
    val result: ToolResultNode?,
    val exchangeIndex: Int,
)

/**
 * A human-centric turn in the conversation.
 *
 * From the user's perspective, a "turn" starts when they type a message
 * and includes ALL the back-and-forth between Auggie and the LLM until
 * the user types again.
 */
data class UserTurn(
    val userMessage: String,
    val thinkingSummary: String?,
    val toolCalls: List<ToolUse>,
    val aiResponse: String,
    val changedFiles: List<String>,
)

// =============================================================================
// Session extension functions for export
// =============================================================================

/**
 * Convert the session to a list of user turns.
 *
 * Groups consecutive exchanges where the user message is empty -
 * these are tool execution loops that are part of the same user turn.
 */
fun Session.toUserTurns(): List<UserTurn> {
    val turns = mutableListOf<UserTurn>()

    var currentUserMessage: String? = null
    var accumulatedThinking: String? = null
    val accumulatedToolCalls = mutableListOf<ToolUse>()
    val accumulatedResponses = mutableListOf<String>()
    val accumulatedChangedFiles = mutableListOf<String>()

    for (history in chatHistory) {
        val exchange = history.exchange
        val userMsg = exchange.requestMessage.trim()

        if (userMsg.isNotEmpty()) {
            // New user message - save the previous turn (if any)
            if (currentUserMessage != null) {
                turns.add(
                    UserTurn(
                        userMessage = currentUserMessage,
                        thinkingSummary = accumulatedThinking,
                        toolCalls = accumulatedToolCalls.toList(),
                        aiResponse = accumulatedResponses
                            .filter { it.isNotBlank() }
                            .joinToString("\n\n"),
                        changedFiles = accumulatedChangedFiles.distinct(),
                    )
                )
            }

            // Start new turn
            currentUserMessage = userMsg
            accumulatedThinking = exchange.getThinkingSummary()
            accumulatedToolCalls.clear()
            accumulatedToolCalls.addAll(exchange.getToolCalls())
            accumulatedResponses.clear()
            accumulatedResponses.add(exchange.responseText)
            accumulatedChangedFiles.clear()
            accumulatedChangedFiles.addAll(history.changedFiles)
        } else {
            // Empty user message - continuation of previous turn (tool loop)
            // Keep first thinking summary if we don't have one yet
            if (accumulatedThinking == null) {
                accumulatedThinking = exchange.getThinkingSummary()
            }
            accumulatedToolCalls.addAll(exchange.getToolCalls())
            accumulatedResponses.add(exchange.responseText)
            accumulatedChangedFiles.addAll(history.changedFiles)
        }
    }

    // Don't forget the last turn
    if (currentUserMessage != null) {
        turns.add(
            UserTurn(
                userMessage = currentUserMessage,
                thinkingSummary = accumulatedThinking,
                toolCalls = accumulatedToolCalls.toList(),
                aiResponse = accumulatedResponses
                    .filter { it.isNotBlank() }
                    .joinToString("\n\n"),
                changedFiles = accumulatedChangedFiles.distinct(),
            )
        )
    }

    return turns
}

/**
 * Get all tool calls paired with their results across the entire session.
 * This links ToolUse from exchange N with ToolResultNode from exchange N+1.
 */
fun Session.getToolCallsWithResults(): List<ToolCallWithResult> {
    val results = mutableListOf<ToolCallWithResult>()

    chatHistory.forEachIndexed { index, history ->
        val toolCalls = history.exchange.getToolCalls()

        // Look for results in the next exchange
        val nextExchange = chatHistory.getOrNull(index + 1)?.exchange
        val toolResults = nextExchange?.requestNodes
            ?.filter { it.type == RequestNodeType.ToolResult }
            ?.mapNotNull { it.toolResultNode }
            ?: emptyList()

        // Pair each tool call with its result (matched by toolUseId)
        toolCalls.forEach { toolUse ->
            val matchingResult = toolResults.find { it.toolUseId == toolUse.toolUseId }
            results.add(ToolCallWithResult(toolUse, matchingResult, index))
        }
    }

    return results
}

// =============================================================================
// Markdown export
// =============================================================================

/**
 * Export a session to a clean Markdown transcript.
 *
 * Uses the human-centric UserTurn structure - each section represents
 * one user message and everything that happened in response.
 */
fun Session.toMarkdown(): String {
    val sb = StringBuilder()

    // Header
    sb.appendLine("# ${customTitle ?: "Session $sessionId"}")
    sb.appendLine()
    sb.appendLine("*Created: $created • Modified: $modified*")
    sb.appendLine()
    sb.appendLine("---")
    sb.appendLine()

    // User turns
    toUserTurns().forEach { turn ->
        // User message
        sb.appendLine("## 👤 User")
        sb.appendLine()
        sb.appendLine(turn.userMessage)
        sb.appendLine()

        // AI thinking (if present and non-empty)
        turn.thinkingSummary?.takeIf { it.isNotBlank() }?.let { thinking ->
            sb.appendLine("### 🧠 Thinking")
            sb.appendLine()
            sb.appendLine(thinking)
            sb.appendLine()
        }

        // Tool calls (if any) - grouped by tool name with count
        if (turn.toolCalls.isNotEmpty()) {
            sb.appendLine("### 🔧 Tools Used")
            sb.appendLine()
            turn.toolCalls
                .groupBy { it.toolName }
                .forEach { (toolName, calls) ->
                    if (calls.size > 1) {
                        sb.appendLine("- **$toolName** (×${calls.size})")
                    } else {
                        sb.appendLine("- **$toolName**")
                    }
                }
            sb.appendLine()
        }

        // AI response (only if non-empty)
        if (turn.aiResponse.isNotBlank()) {
            sb.appendLine("### 🤖 Assistant")
            sb.appendLine()
            sb.appendLine(turn.aiResponse)
            sb.appendLine()
        }

        // Changed files (if any)
        if (turn.changedFiles.isNotEmpty()) {
            sb.appendLine("### 📝 Files Changed")
            sb.appendLine()
            turn.changedFiles.forEach { file ->
                sb.appendLine("- `$file`")
            }
            sb.appendLine()
        }

        sb.appendLine("---")
        sb.appendLine()
    }

    return sb.toString()
}

