---
created: 2026-03-04
related:
  - "[[patterns]]"
  - "[[SessionExporter]]"
  - "[[Markdown]]"
---

When generating [[Markdown]] blockquotes programmatically, avoid trailing spaces on empty continuation lines.

**Correct**: `">"` for empty lines
**Wrong**: `"> "` (trailing space)

**Pattern for multi-line blockquotes**:
```kotlin
sb.appendLine("> **Header**")
sb.appendLine(">")  // Empty continuation - no trailing space
content.lines().forEach { line ->
    sb.appendLine("> $line".trimEnd())  // trimEnd() handles empty lines
}
```

**Why `trimEnd()`**: When `line` is empty, `"> $line"` becomes `"> "` (with trailing space). Using `trimEnd()` converts it to `">"`.

**Example output**:
```markdown
> 🧠 **Thinking**
>
> The user wants to implement...
> Let me look at the code.
>
> I should check the tests too.
```

Each empty line is `>` not `> `.

