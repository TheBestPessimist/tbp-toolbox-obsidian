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
 * Represents a single step in the AI's processing.
 * Preserves the order: thinking -> tool calls -> response
 */
data class AssistantStep(
    val thinking: String?,
    val toolCalls: List<ToolUse>,
    val response: String,
    val changedFiles: List<String>,
)

/**
 * A human-centric turn in the conversation.
 *
 * From the user's perspective, a "turn" starts when they type a message
 * and includes ALL the back-and-forth between Auggie and the LLM until
 * the user types again.
 *
 * The `steps` list preserves the chronological order of the AI's work.
 */
data class UserTurn(
    val userMessage: String,
    val steps: List<AssistantStep>,
) {
    /** All unique thinking summaries in this turn */
    val allThinking: List<String>
        get() = steps.mapNotNull { it.thinking }.filter { it.isNotBlank() }.distinct()

    /** All tool calls made in this turn, in order */
    val allToolCalls: List<ToolUse>
        get() = steps.flatMap { it.toolCalls }

    /** All non-empty responses, in order */
    val allResponses: List<String>
        get() = steps.map { it.response }.filter { it.isNotBlank() }

    /** All changed files, deduplicated */
    val allChangedFiles: List<String>
        get() = steps.flatMap { it.changedFiles }.distinct()
}

// =============================================================================
// Session extension functions for export
// =============================================================================

/**
 * Convert the session to a list of user turns.
 *
 * Groups consecutive exchanges where the user message is empty -
 * these are tool execution loops that are part of the same user turn.
 * Preserves chronological order of AI steps within each turn.
 */
fun Session.toUserTurns(): List<UserTurn> {
    val turns = mutableListOf<UserTurn>()

    var currentUserMessage: String? = null
    val currentSteps = mutableListOf<AssistantStep>()

    for (history in chatHistory) {
        val exchange = history.exchange
        val userMsg = exchange.requestMessage.trim()

        if (userMsg.isNotEmpty()) {
            // New user message - save the previous turn (if any)
            if (currentUserMessage != null) {
                turns.add(
                    UserTurn(
                        userMessage = currentUserMessage,
                        steps = currentSteps.toList(),
                    )
                )
            }

            // Start new turn
            currentUserMessage = userMsg
            currentSteps.clear()
        }

        // Add this exchange's content as a step (for both new and continuation)
        if (currentUserMessage != null) {
            currentSteps.add(
                AssistantStep(
                    thinking = exchange.getThinkingSummary(),
                    toolCalls = exchange.getToolCalls(),
                    response = exchange.responseText,
                    changedFiles = history.changedFiles,
                )
            )
        }
    }

    // Don't forget the last turn
    if (currentUserMessage != null) {
        turns.add(
            UserTurn(
                userMessage = currentUserMessage,
                steps = currentSteps.toList(),
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
 * Designed for both:
 * - Humans to read and understand what happened
 * - LLMs to use as context to resume work on the topic
 *
 * Preserves chronological order within each user turn.
 */
fun Session.toMarkdown(): String {
    val sb = StringBuilder()

    // Header with context for LLMs
    sb.appendLine("# ${customTitle ?: "Session $sessionId"}")
    sb.appendLine()
    sb.appendLine("**Session ID:** `$sessionId`")
    sb.appendLine("**Created:** $created")
    sb.appendLine("**Modified:** $modified")
    sb.appendLine()

    // User turns
    toUserTurns().forEach { turn ->
        // User message
        sb.appendLine("## 👤 User")
        sb.appendLine()
        sb.appendLine(turn.userMessage)
        sb.appendLine()

        // Track which thinking summaries we've already shown
        val shownThinking = mutableSetOf<String>()

        // Process steps in chronological order
        turn.steps.forEach { step ->
            // Thinking (if present and not a duplicate)
            step.thinking?.takeIf { it.isNotBlank() && it !in shownThinking }?.let { thinking ->
                shownThinking.add(thinking)
                sb.appendLine("> 🧠 **Thinking**")
                sb.appendLine(">")
                thinking.lines().forEach { line ->
                    sb.appendLine("> $line".trimEnd())
                }
                sb.appendLine()
            }

            // Tool calls with details
            step.toolCalls.forEach { tool ->
                sb.appendLine("> 🔧 **${tool.toolName}**")
                sb.appendLine(">")
                sb.appendLine("> ```json")
                formatToolInput(tool.inputJson).lines().forEach { line ->
                    sb.appendLine("> $line".trimEnd())
                }
                sb.appendLine("> ```")
                sb.appendLine()
            }

            // Response (if non-empty)
            if (step.response.isNotBlank()) {
                sb.appendLine("> 🤖 **Assistant**")
                sb.appendLine(">")
                step.response.lines().forEach { line ->
                    sb.appendLine("> $line".trimEnd())
                }
                sb.appendLine()
            }
        }

        // Changed files summary (deduplicated across all steps)
        if (turn.allChangedFiles.isNotEmpty()) {
            sb.appendLine("### 📝 Files Changed")
            sb.appendLine()
            turn.allChangedFiles.forEach { file ->
                sb.appendLine("- `$file`")
            }
            sb.appendLine()
        }
    }

    return sb.toString()
}

/**
 * Format tool input JSON for readability.
 * Pretty-prints if it's valid JSON, otherwise returns as-is.
 */
private fun formatToolInput(inputJson: String): String {
    return try {
        // Try to parse and pretty-print
        val element = kotlinx.serialization.json.Json.parseToJsonElement(inputJson)
        kotlinx.serialization.json.Json { prettyPrint = true }.encodeToString(
            kotlinx.serialization.json.JsonElement.serializer(),
            element
        )
    } catch (e: Exception) {
        // If parsing fails, return as-is
        inputJson
    }
}

