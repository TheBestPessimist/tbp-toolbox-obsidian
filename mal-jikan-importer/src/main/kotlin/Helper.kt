

 fun writeFrontmatter(frontmatter: Map<String, String>, body: String): String {
    val sb = StringBuilder()
    sb.appendLine("---")
    frontmatter.forEach { (key, value) ->
        sb.appendLine("$key: $value")
    }
    sb.appendLine("---")
    if (body.isNotEmpty()) {
        sb.appendLine()
        sb.append(body)
    }
    return sb.toString()
}

 fun extractFrontmatter(content: String): Pair<MutableMap<String, String>, String> {
     val lines = content.lines()
     if (lines.firstOrNull() != "---") {
         return mutableMapOf<String, String>() to content
     }

     val endIndex = lines.drop(1).indexOfFirst { it == "---" }
     if (endIndex == -1) {
         return mutableMapOf<String, String>() to content
     }

     val frontmatterLines = lines.subList(1, endIndex + 1)
     val frontmatter = frontmatterLines.associate { line ->
         val parts = line.split(":", limit = 2)
         if (parts.size == 2) {
             parts[0].trim() to parts[1].trim()
         } else {
             parts[0].trim() to ""
         }
     }.toMutableMap()

     val bodyStartIndex = endIndex + 2
     val body = if (bodyStartIndex < lines.size) {
         lines.subList(bodyStartIndex, lines.size).joinToString("\n")
     } else {
         ""
     }

     return frontmatter to body
 }
