---
created: 2026-03-04
related:
  - "[[gotchas]]"
  - "[[Augment CLI]]"
  - "[[SessionExporter]]"
  - "[[response_nodes]]"
---

The `response_nodes` array in [[Augment CLI]] session JSON files has items in **execution order**, not **display order**.

**JSON structure** (execution order):
```
response_nodes:
  [0] type=Thinking    ← AI reasoning
  [1] type=ToolUse     ← Tool calls happen
  [2] type=ToolUse
  [3] type=ToolUse
  [4] type=TokenUsage  ← Stats
  [5] type=Text        ← AI's text response comes LAST
```

**Display order** (what [[Augment CLI]] UI shows):
```
~ Thinking summary
● AI's text response    ← Text shown BEFORE tools
● Tool call 1
● Tool call 2
● Tool call 3
```

**Why this matters**: If you process `response_nodes` in JSON order, text appears after tool calls. But users expect text before tool calls (that's how the UI shows it).

**Solution**: Reorder items when processing:
```kotlin
val thinking = items.filterIsInstance<ResponseItem.Thinking>()
val text = items.filterIsInstance<ResponseItem.Text>()
val tools = items.filterIsInstance<ResponseItem.Tool>()
return thinking + text + tools
```

**Debug tip**: Print `response_nodes` types and content to see actual structure:
```kotlin
exchange.responseNodes.forEachIndexed { idx, node ->
    println("[$idx] type=${node.type}, content='${node.content.take(60)}'")
}
```

