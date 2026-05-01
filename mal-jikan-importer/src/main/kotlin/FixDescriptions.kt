import java.io.File

fun main() {
    val directory = File("""D:\all\notes\Anime\MAL dump""")
    val mdFiles = directory.listFiles { file -> file.extension == "md" }?.sortedBy { it.name } ?: emptyList()

    println("Found ${mdFiles.size} markdown files to process")

    mdFiles.forEachIndexed { index, file ->

        println("\n[${index + 1}/${mdFiles.size}] Processing: ${file.name}")

        try {
            val content = file.readText()
            val (frontmatter, body) = extractFrontmatter(content)

            val mal_synopsis = frontmatter["mal_synopsis"]!!

            if(mal_synopsis.isNullOrBlank()) return@forEachIndexed

            var fixed: String = mal_synopsis
            println(mal_synopsis)

            fixed = fixed.replace(""""""", """\"""")
            fixed = if (fixed.startsWith(""""""")) fixed else """"$fixed"""
            fixed = if (fixed.endsWith(""""""")) fixed else """$fixed""""
            println(
                if (mal_synopsis == fixed) "has quotes"
                else "no quotes",
            )

            frontmatter["mal_synopsis"] = fixed

            val updatedContent = writeFrontmatter(frontmatter, body)
            file.writeText(updatedContent)
        } catch (e: Exception) {
            println("  ✗ Error: ${e.message}")
            e.printStackTrace()
        }
    }
}
