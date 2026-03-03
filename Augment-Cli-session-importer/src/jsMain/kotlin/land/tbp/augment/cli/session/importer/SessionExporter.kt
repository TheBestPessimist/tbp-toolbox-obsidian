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
 * A clean representation of an exchange for transcript purposes.
 * Strips duplicated data and provides easy access to important fields.
 */
data class CleanExchange(
    val index: Int,
    val userMessage: String,
    val thinkingSummary: String?,
    val toolCalls: List<ToolUse>,
    val aiResponse: String,
    val finishedAt: String,
    val changedFiles: List<String>,
    val completed: Boolean,
)

// =============================================================================
// Session extension functions for export
// =============================================================================

/**
 * Convert the session to a list of clean exchanges without duplication.
 */
fun Session.toCleanExchanges(): List<CleanExchange> {
    return chatHistory.mapIndexed { index, history ->
        CleanExchange(
            index = index,
            userMessage = history.exchange.requestMessage,
            thinkingSummary = history.exchange.getThinkingSummary(),
            toolCalls = history.exchange.getToolCalls(),
            aiResponse = history.exchange.responseText,
            finishedAt = history.finishedAt,
            changedFiles = history.changedFiles,
            completed = history.completed,
        )
    }
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
 */
fun Session.toMarkdown(): String {
    val sb = StringBuilder()

    // Header
    sb.appendLine("# Session: ${customTitle ?: sessionId}")
    sb.appendLine()
    sb.appendLine("- **Created:** $created")
    sb.appendLine("- **Modified:** $modified")
    sb.appendLine("- **Session ID:** $sessionId")
    sb.appendLine()
    sb.appendLine("---")
    sb.appendLine()

    // Exchanges
    toCleanExchanges().forEach { exchange ->
        sb.appendLine("## Exchange ${exchange.index + 1}")
        sb.appendLine()

        // User message
        sb.appendLine("### 👤 User")
        sb.appendLine()
        sb.appendLine(exchange.userMessage)
        sb.appendLine()

        // AI thinking (if present)
        exchange.thinkingSummary?.let { thinking ->
            sb.appendLine("### 🧠 Thinking")
            sb.appendLine()
            sb.appendLine(thinking)
            sb.appendLine()
        }

        // Tool calls (if any)
        if (exchange.toolCalls.isNotEmpty()) {
            sb.appendLine("### 🔧 Tool Calls")
            sb.appendLine()
            exchange.toolCalls.forEach { tool ->
                sb.appendLine("- **${tool.toolName}**")
            }
            sb.appendLine()
        }

        // AI response
        sb.appendLine("### 🤖 Assistant")
        sb.appendLine()
        sb.appendLine(exchange.aiResponse)
        sb.appendLine()

        // Changed files (if any)
        if (exchange.changedFiles.isNotEmpty()) {
            sb.appendLine("#### Changed Files")
            sb.appendLine()
            exchange.changedFiles.forEach { file ->
                sb.appendLine("- `$file`")
            }
            sb.appendLine()
        }

        sb.appendLine("---")
        sb.appendLine()
    }

    return sb.toString()
}

