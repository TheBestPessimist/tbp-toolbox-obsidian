package land.tbp.augment.cli.session.importer

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

/**
 * Deserialization tests for AugmentCliSessionDto classes.
 * These tests ensure backwards compatibility when session file formats change.
 */
class AugmentCliSessionDtoTest : StringSpec({

    val json = Json {
        ignoreUnknownKeys = true
    }

    // ========================================================================
    // Session Tests
    // ========================================================================

    "should deserialize minimal session" {
        val sessionJson = """
        {
            "sessionId": "test-session-id",
            "created": "2026-01-16T12:00:00.000Z",
            "modified": "2026-01-16T12:30:00.000Z",
            "chatHistory": [],
            "agentState": {
                "userGuidelines": "",
                "workspaceGuidelines": "",
                "agentMemories": "",
                "modelId": "claude-opus-4-5",
                "userEmail": "test@example.com"
            },
            "rootTaskUuid": "task-uuid-123"
        }
        """.trimIndent()

        val session = json.decodeFromString<Session>(sessionJson)
        session.sessionId shouldBe "test-session-id"
        session.customTitle.shouldBeNull()
        session.terminalId.shouldBeNull()
    }

    "should deserialize session with optional fields" {
        val sessionJson = """
        {
            "sessionId": "test-session-id",
            "created": "2026-01-16T12:00:00.000Z",
            "modified": "2026-01-16T12:30:00.000Z",
            "chatHistory": [],
            "agentState": {
                "userGuidelines": "Be helpful",
                "workspaceGuidelines": "Follow coding standards",
                "agentMemories": "User prefers Kotlin",
                "modelId": "claude-opus-4-5",
                "userEmail": "test@example.com"
            },
            "rootTaskUuid": "task-uuid-123",
            "customTitle": "My Custom Session Title",
            "terminalId": "/dev/cons0"
        }
        """.trimIndent()

        val session = json.decodeFromString<Session>(sessionJson)
        session.customTitle shouldBe "My Custom Session Title"
        session.terminalId shouldBe "/dev/cons0"
    }

    // ========================================================================
    // ChatHistory Tests
    // ========================================================================

    "should deserialize chat history with fractional sequenceId" {
        val chatHistoryJson = """
        {
            "exchange": {
                "request_message": "test",
                "response_text": "response",
                "request_id": "req-123",
                "request_nodes": [],
                "response_nodes": []
            },
            "completed": true,
            "sequenceId": 5.5,
            "finishedAt": "2026-01-16T12:00:00.000Z",
            "changedFiles": [],
            "changedFilesSkipped": [],
            "changedFilesSkippedCount": 0
        }
        """.trimIndent()

        val chatHistory = json.decodeFromString<ChatHistory>(chatHistoryJson)
        chatHistory.sequenceId shouldBe 5.5
        chatHistory.isHistorySummary.shouldBeNull()
        chatHistory.historySummaryVersion.shouldBeNull()
        chatHistory.source.shouldBeNull()
    }

    "should deserialize chat history with optional fields" {
        val chatHistoryJson = """
        {
            "exchange": {
                "request_message": "test",
                "response_text": "response",
                "request_id": "req-123",
                "request_nodes": [],
                "response_nodes": []
            },
            "completed": true,
            "sequenceId": 1,
            "finishedAt": "2026-01-16T12:00:00.000Z",
            "changedFiles": ["file1.kt"],
            "changedFilesSkipped": ["file2.kt"],
            "changedFilesSkippedCount": 1,
            "isHistorySummary": false,
            "historySummaryVersion": 0,
            "source": "local"
        }
        """.trimIndent()

        val chatHistory = json.decodeFromString<ChatHistory>(chatHistoryJson)
        chatHistory.isHistorySummary shouldBe false
        chatHistory.historySummaryVersion shouldBe 0
        chatHistory.source shouldBe "local"
        chatHistory.changedFiles shouldHaveSize 1
    }

    // ========================================================================
    // RequestNode Tests - Type 0 (Text)
    // ========================================================================

    "should deserialize request node type 0 - text node" {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 0,
            "text_node": {
                "content": "Hello, this is the user's message"
            }
        }
        """.trimIndent()

        val node = json.decodeFromString<RequestNode>(requestNodeJson)
        node.id shouldBe 1
        node.type shouldBe 0
        node.textNode.shouldNotBeNull()
        node.textNode!!.content shouldBe "Hello, this is the user's message"
        node.toolResultNode.shouldBeNull()
        node.ideStateNode.shouldBeNull()
    }

    // ========================================================================
    // RequestNode Tests - Type 1 (ToolResult)
    // ========================================================================

    "should deserialize request node type 1 - tool result node minimal" {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 1,
            "tool_result_node": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "content": "Here's the file content...",
                "is_error": false
            }
        }
        """.trimIndent()

        val node = json.decodeFromString<RequestNode>(requestNodeJson)
        node.type shouldBe 1
        node.toolResultNode.shouldNotBeNull()
        node.toolResultNode!!.toolUseId shouldBe "toolu_vrtx_01ABC123"
        node.toolResultNode!!.content shouldBe "Here's the file content..."
        node.toolResultNode!!.isError shouldBe false
        node.toolResultNode!!.durationMs.shouldBeNull()
        node.toolResultNode!!.metadata.shouldBeNull()
    }

    "should deserialize request node type 1 - tool result node with all fields" {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 1,
            "tool_result_node": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "content": "File edited successfully",
                "is_error": false,
                "duration_ms": 410,
                "start_time_ms": 1772197681596,
                "request_id": "req-456",
                "metadata": {
                    "tool_lines_added": 15,
                    "tool_lines_deleted": 3
                }
            }
        }
        """.trimIndent()

        val node = json.decodeFromString<RequestNode>(requestNodeJson)
        node.toolResultNode.shouldNotBeNull()
        node.toolResultNode!!.durationMs shouldBe 410
        node.toolResultNode!!.startTimeMs shouldBe 1772197681596
        node.toolResultNode!!.requestId shouldBe "req-456"
        node.toolResultNode!!.metadata.shouldNotBeNull()
        node.toolResultNode!!.metadata!!.toolLinesAdded shouldBe 15
        node.toolResultNode!!.metadata!!.toolLinesDeleted shouldBe 3
    }

    "should deserialize request node type 1 - tool result with error" {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 1,
            "tool_result_node": {
                "tool_use_id": "toolu_vrtx_01XYZ789",
                "content": "Command not found: obsidian",
                "is_error": true,
                "duration_ms": 699,
                "start_time_ms": 1772213615599
            }
        }
        """.trimIndent()

        val node = json.decodeFromString<RequestNode>(requestNodeJson)
        node.toolResultNode!!.isError shouldBe true
    }

    // ========================================================================
    // RequestNode Tests - Type 4 (IdeState)
    // ========================================================================

    "should deserialize request node type 4 - ide state node" {
        val requestNodeJson = """
        {
            "id": 2,
            "type": 4,
            "ide_state_node": {
                "workspace_folders": [
                    {
                        "repository_root": "d:\\all\\work\\MyProject",
                        "folder_root": "d:\\all\\work\\MyProject"
                    }
                ],
                "workspace_folders_unchanged": false,
                "current_terminal": {
                    "terminal_id": 0,
                    "current_working_directory": "d:\\all\\work\\MyProject"
                }
            }
        }
        """.trimIndent()

        val node = json.decodeFromString<RequestNode>(requestNodeJson)
        node.type shouldBe 4
        node.ideStateNode.shouldNotBeNull()
        node.ideStateNode!!.workspaceFolders shouldHaveSize 1
        node.ideStateNode!!.workspaceFolders[0].repositoryRoot shouldBe "d:\\all\\work\\MyProject"
        node.ideStateNode!!.workspaceFoldersUnchanged shouldBe false
        node.ideStateNode!!.currentTerminal.terminalId shouldBe 0
        node.ideStateNode!!.currentTerminal.currentWorkingDirectory shouldBe "d:\\all\\work\\MyProject"
    }

    // ========================================================================
    // ResponseNode Tests - Type 0 (Text)
    // ========================================================================

    "should deserialize response node type 0 - text content" {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 0,
            "content": "Here is the response from the LLM",
            "tool_use": null,
            "thinking": null,
            "billing_metadata": null,
            "metadata": {
                "openai_id": null,
                "google_ts": null,
                "provider": "anthropic"
            },
            "token_usage": null,
            "timestamp_ms": 1772197681581
        }
        """.trimIndent()

        val node = json.decodeFromString<ResponseNode>(responseNodeJson)
        node.type shouldBe 0
        node.content shouldBe "Here is the response from the LLM"
        node.toolUse.shouldBeNull()
        node.thinking.shouldBeNull()
        node.metadata.shouldNotBeNull()
        node.metadata!!.provider shouldBe "anthropic"
        node.timestampMs shouldBe 1772197681581
    }

    // ========================================================================
    // ResponseNode Tests - Type 5 (ToolUse)
    // ========================================================================

    "should deserialize response node type 5 - tool use minimal" {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 5,
            "content": "",
            "tool_use": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "tool_name": "view",
                "input_json": "{\"path\": \".\", \"type\": \"directory\"}",
                "is_partial": false
            },
            "thinking": null,
            "billing_metadata": null,
            "metadata": null,
            "token_usage": null
        }
        """.trimIndent()

        val node = json.decodeFromString<ResponseNode>(responseNodeJson)
        node.type shouldBe 5
        node.toolUse.shouldNotBeNull()
        node.toolUse!!.toolUseId shouldBe "toolu_vrtx_01ABC123"
        node.toolUse!!.toolName shouldBe "view"
        node.toolUse!!.inputJson shouldBe "{\"path\": \".\", \"type\": \"directory\"}"
        node.toolUse!!.isPartial shouldBe false
        node.toolUse!!.startedAtMs.shouldBeNull()
        node.toolUse!!.completedAtMs.shouldBeNull()
    }

    "should deserialize response node type 5 - tool use with timing" {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 5,
            "content": "",
            "tool_use": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "tool_name": "launch-process",
                "input_json": "{\"command\": \"Get-Date\", \"cwd\": \"d:\\\\all\\\\notes\", \"wait\": true}",
                "is_partial": false,
                "started_at_ms": 1772197681596,
                "completed_at_ms": 1772197682006
            },
            "thinking": null,
            "metadata": {
                "provider": null
            }
        }
        """.trimIndent()

        val node = json.decodeFromString<ResponseNode>(responseNodeJson)
        node.toolUse!!.startedAtMs shouldBe 1772197681596
        node.toolUse!!.completedAtMs shouldBe 1772197682006
    }

    // ========================================================================
    // ResponseNode Tests - Type 8 (Thinking)
    // ========================================================================

    "should deserialize response node type 8 - thinking" {
        val responseNodeJson = """
        {
            "id": 0,
            "type": 8,
            "content": "",
            "tool_use": null,
            "thinking": {
                "summary": "The user wants me to see a TODO file. Let me search for it.",
                "encrypted_content": "EsECCkgICxACGAIqQKwV9Ej8245MvW4...",
                "content": null,
                "openai_responses_api_item_id": null
            },
            "billing_metadata": null,
            "metadata": {
                "openai_id": null,
                "google_ts": null,
                "provider": "anthropic"
            },
            "token_usage": null,
            "timestamp_ms": 1772197680810
        }
        """.trimIndent()

        val node = json.decodeFromString<ResponseNode>(responseNodeJson)
        node.type shouldBe 8
        node.thinking.shouldNotBeNull()
        node.thinking!!.summary shouldBe "The user wants me to see a TODO file. Let me search for it."
        node.thinking!!.encryptedContent shouldBe "EsECCkgICxACGAIqQKwV9Ej8245MvW4..."
        node.thinking!!.content.shouldBeNull()
        node.thinking!!.openaiResponsesApiItemId.shouldBeNull()
    }

    "should deserialize thinking with decrypted content" {
        val thinkingJson = """
        {
            "summary": "Analyzing the code",
            "encrypted_content": "encrypted...",
            "content": "This is the decrypted thinking content",
            "openai_responses_api_item_id": "resp_abc123"
        }
        """.trimIndent()

        val thinking = json.decodeFromString<Thinking>(thinkingJson)
        thinking.content shouldBe "This is the decrypted thinking content"
        thinking.openaiResponsesApiItemId shouldBe "resp_abc123"
    }

    // ========================================================================
    // ResponseNode Tests - Type 10 (TokenUsage)
    // ========================================================================

    "should deserialize response node type 10 - token usage" {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 10,
            "content": "",
            "tool_use": null,
            "thinking": null,
            "billing_metadata": null,
            "metadata": null,
            "token_usage": {
                "input_tokens": 9,
                "output_tokens": 171,
                "cache_read_input_tokens": 9986,
                "cache_creation_input_tokens": 549,
                "system_prompt_tokens": 4087,
                "chat_history_tokens": 0,
                "current_message_tokens": 20,
                "max_context_tokens": 204800,
                "tool_definitions_tokens": 7387,
                "tool_result_tokens": 0,
                "assistant_response_tokens": 171
            },
            "timestamp_ms": 1772197681579
        }
        """.trimIndent()

        val node = json.decodeFromString<ResponseNode>(responseNodeJson)
        node.type shouldBe 10
        node.tokenUsage.shouldNotBeNull()
        node.tokenUsage!!.inputTokens shouldBe 9
        node.tokenUsage!!.outputTokens shouldBe 171
        node.tokenUsage!!.cacheReadInputTokens shouldBe 9986
        node.tokenUsage!!.cacheCreationInputTokens shouldBe 549
        node.tokenUsage!!.systemPromptTokens shouldBe 4087
        node.tokenUsage!!.chatHistoryTokens shouldBe 0
        node.tokenUsage!!.currentMessageTokens shouldBe 20
        node.tokenUsage!!.maxContextTokens shouldBe 204800
        node.tokenUsage!!.toolDefinitionsTokens shouldBe 7387
        node.tokenUsage!!.toolResultTokens shouldBe 0
        node.tokenUsage!!.assistantResponseTokens shouldBe 171
    }

    "should deserialize token usage with optional fields missing" {
        val tokenUsageJson = """
        {
            "input_tokens": 10,
            "output_tokens": 50,
            "cache_read_input_tokens": 1000,
            "cache_creation_input_tokens": 100,
            "system_prompt_tokens": 500,
            "chat_history_tokens": 200,
            "current_message_tokens": 30,
            "max_context_tokens": 100000
        }
        """.trimIndent()

        val tokenUsage = json.decodeFromString<TokenUsage>(tokenUsageJson)
        tokenUsage.inputTokens shouldBe 10
        tokenUsage.toolDefinitionsTokens.shouldBeNull()
        tokenUsage.toolResultTokens.shouldBeNull()
        tokenUsage.assistantResponseTokens.shouldBeNull()
    }

    // ========================================================================
    // Metadata Tests
    // ========================================================================

    "should deserialize metadata with all fields" {
        val metadataJson = """
        {
            "openai_id": "chatcmpl-abc123",
            "google_ts": "2026-01-16T12:00:00Z",
            "provider": "anthropic",
            "phase": "generation"
        }
        """.trimIndent()

        val metadata = json.decodeFromString<Metadata>(metadataJson)
        metadata.openaiId shouldBe "chatcmpl-abc123"
        metadata.googleTs shouldBe "2026-01-16T12:00:00Z"
        metadata.provider shouldBe "anthropic"
        metadata.phase shouldBe "generation"
    }

    "should deserialize metadata with all null fields" {
        val metadataJson = """
        {
            "openai_id": null,
            "google_ts": null,
            "provider": null,
            "phase": null
        }
        """.trimIndent()

        val metadata = json.decodeFromString<Metadata>(metadataJson)
        metadata.openaiId.shouldBeNull()
        metadata.googleTs.shouldBeNull()
        metadata.provider.shouldBeNull()
        metadata.phase.shouldBeNull()
    }

    // ========================================================================
    // Full Session Integration Test
    // ========================================================================

    "should deserialize full session with all node types" {
        val fullSessionJson = """
        {
            "sessionId": "full-test-session",
            "created": "2026-01-16T12:00:00.000Z",
            "modified": "2026-01-16T12:30:00.000Z",
            "chatHistory": [
                {
                    "exchange": {
                        "request_message": "See TODO",
                        "response_text": "Here is the TODO file content",
                        "request_id": "req-001",
                        "request_nodes": [
                            {
                                "id": 1,
                                "type": 0,
                                "text_node": { "content": "See TODO" }
                            },
                            {
                                "id": 2,
                                "type": 4,
                                "ide_state_node": {
                                    "workspace_folders": [
                                        { "repository_root": "d:\\project", "folder_root": "d:\\project" }
                                    ],
                                    "workspace_folders_unchanged": false,
                                    "current_terminal": { "terminal_id": 0, "current_working_directory": "d:\\project" }
                                }
                            }
                        ],
                        "response_nodes": [
                            {
                                "id": 0,
                                "type": 8,
                                "content": "",
                                "thinking": {
                                    "summary": "Looking for TODO file",
                                    "encrypted_content": "encrypted..."
                                },
                                "metadata": { "provider": "anthropic" }
                            },
                            {
                                "id": 1,
                                "type": 5,
                                "content": "",
                                "tool_use": {
                                    "tool_use_id": "toolu_001",
                                    "tool_name": "view",
                                    "input_json": "{\"path\": \"TODO.txt\"}",
                                    "is_partial": false
                                }
                            },
                            {
                                "id": 2,
                                "type": 10,
                                "content": "",
                                "token_usage": {
                                    "input_tokens": 100,
                                    "output_tokens": 200,
                                    "cache_read_input_tokens": 5000,
                                    "cache_creation_input_tokens": 500,
                                    "system_prompt_tokens": 4000,
                                    "chat_history_tokens": 100,
                                    "current_message_tokens": 50,
                                    "max_context_tokens": 200000
                                }
                            },
                            {
                                "id": 3,
                                "type": 0,
                                "content": "Here is the TODO file content",
                                "metadata": { "provider": "anthropic" }
                            }
                        ]
                    },
                    "completed": true,
                    "sequenceId": 1,
                    "finishedAt": "2026-01-16T12:05:00.000Z",
                    "changedFiles": [],
                    "changedFilesSkipped": [],
                    "changedFilesSkippedCount": 0,
                    "source": "local"
                },
                {
                    "exchange": {
                        "request_message": "",
                        "response_text": "File viewed successfully",
                        "request_id": "req-002",
                        "request_nodes": [
                            {
                                "id": 1,
                                "type": 4,
                                "ide_state_node": {
                                    "workspace_folders": [
                                        { "repository_root": "d:\\project", "folder_root": "d:\\project" }
                                    ],
                                    "workspace_folders_unchanged": true,
                                    "current_terminal": { "terminal_id": 0, "current_working_directory": "d:\\project" }
                                }
                            },
                            {
                                "id": 1,
                                "type": 1,
                                "tool_result_node": {
                                    "tool_use_id": "toolu_001",
                                    "content": "TODO.txt contents here...",
                                    "is_error": false,
                                    "duration_ms": 50
                                }
                            }
                        ],
                        "response_nodes": [
                            {
                                "id": 1,
                                "type": 0,
                                "content": "File viewed successfully",
                                "metadata": { "provider": "anthropic" }
                            }
                        ]
                    },
                    "completed": true,
                    "sequenceId": 2,
                    "finishedAt": "2026-01-16T12:10:00.000Z",
                    "changedFiles": [],
                    "changedFilesSkipped": [],
                    "changedFilesSkippedCount": 0
                }
            ],
            "agentState": {
                "userGuidelines": "Be helpful",
                "workspaceGuidelines": "",
                "agentMemories": "",
                "modelId": "claude-opus-4-5",
                "userEmail": "test@example.com"
            },
            "rootTaskUuid": "root-task-uuid",
            "customTitle": "TODO File Review"
        }
        """.trimIndent()

        val session = json.decodeFromString<Session>(fullSessionJson)

        // Verify session level
        session.sessionId shouldBe "full-test-session"
        session.customTitle shouldBe "TODO File Review"
        session.chatHistory shouldHaveSize 2

        // Verify first chat history entry
        val firstChat = session.chatHistory[0]
        firstChat.exchange.requestNodes shouldHaveSize 2
        firstChat.exchange.responseNodes shouldHaveSize 4
        firstChat.source shouldBe "local"

        // Verify request nodes
        firstChat.exchange.requestNodes[0].type shouldBe 0
        firstChat.exchange.requestNodes[0].textNode!!.content shouldBe "See TODO"
        firstChat.exchange.requestNodes[1].type shouldBe 4
        firstChat.exchange.requestNodes[1].ideStateNode!!.workspaceFoldersUnchanged shouldBe false

        // Verify response nodes
        firstChat.exchange.responseNodes[0].type shouldBe 8  // Thinking
        firstChat.exchange.responseNodes[0].thinking!!.summary shouldBe "Looking for TODO file"
        firstChat.exchange.responseNodes[1].type shouldBe 5  // ToolUse
        firstChat.exchange.responseNodes[1].toolUse!!.toolName shouldBe "view"
        firstChat.exchange.responseNodes[2].type shouldBe 10 // TokenUsage
        firstChat.exchange.responseNodes[2].tokenUsage!!.inputTokens shouldBe 100
        firstChat.exchange.responseNodes[3].type shouldBe 0  // Text

        // Verify second chat history entry with tool result
        val secondChat = session.chatHistory[1]
        secondChat.exchange.requestNodes shouldHaveSize 2
        secondChat.exchange.requestNodes[1].type shouldBe 1
        secondChat.exchange.requestNodes[1].toolResultNode!!.toolUseId shouldBe "toolu_001"
        secondChat.exchange.requestNodes[1].toolResultNode!!.isError shouldBe false
        secondChat.exchange.requestNodes[1].toolResultNode!!.durationMs shouldBe 50
    }
})

