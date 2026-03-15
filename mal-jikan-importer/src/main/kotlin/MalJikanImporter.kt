import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

@Serializable
data class AnimeResponse(
    val data: AnimeData
)

@Serializable
data class AnimeData(
    val mal_id: Int,
    val url: String,
    val images: Images,
    val score: Double?,
    val synopsis: String?
)

@Serializable
data class Images(
    val jpg: JpgImage
)

@Serializable
data class JpgImage(
    val image_url: String,
    val small_image_url: String,
    val large_image_url: String
)

fun extractFrontmatter(content: String): Pair<Map<String, String>, String> {
    val lines = content.lines()
    if (lines.firstOrNull() != "---") {
        return emptyMap<String, String>() to content
    }
    
    val endIndex = lines.drop(1).indexOfFirst { it == "---" }
    if (endIndex == -1) {
        return emptyMap<String, String>() to content
    }
    
    val frontmatterLines = lines.subList(1, endIndex + 1)
    val frontmatter = frontmatterLines.associate { line ->
        val parts = line.split(":", limit = 2)
        if (parts.size == 2) {
            parts[0].trim() to parts[1].trim()
        } else {
            parts[0].trim() to ""
        }
    }
    
    val bodyStartIndex = endIndex + 2
    val body = if (bodyStartIndex < lines.size) {
        lines.subList(bodyStartIndex, lines.size).joinToString("\n")
    } else {
        ""
    }
    
    return frontmatter to body
}

fun updateFrontmatter(
    frontmatter: Map<String, String>,
    imageUrl: String,
    url: String,
    score: Double?,
    synopsis: String?
): Map<String, String> {
    val mutable = frontmatter.toMutableMap()
    mutable["mal_image_url"] = imageUrl
    mutable["mal_url"] = url
    mutable["mal_score"] = score?.toString() ?: ""
    mutable["mal_synopsis"] = synopsis?.replace("\n", " ")?.replace("\r", "") ?: ""
    return mutable
}

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

fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    val directory = File("D:\\all\\notes\\temp\\CSV Import")
    val mdFiles = directory.listFiles { file -> file.extension == "md" }?.sortedBy { it.name } ?: emptyList()
    
    println("Found ${mdFiles.size} markdown files to process")
    
    mdFiles.forEachIndexed { index, file ->
        println("\n[${index + 1}/${mdFiles.size}] Processing: ${file.name}")
        
        try {
            val content = file.readText()
            val (frontmatter, body) = extractFrontmatter(content)
            
            val seriesAnimeDbId = frontmatter["series_animedb_id"]
            if (seriesAnimeDbId.isNullOrBlank()) {
                println("  ⚠ Skipping: No series_animedb_id found")
                return@forEachIndexed
            }
            
            println("  → Fetching data for anime ID: $seriesAnimeDbId")
            val response: AnimeResponse = client.get("https://api.jikan.moe/v4/anime/$seriesAnimeDbId").body()
            
            val updatedFrontmatter = updateFrontmatter(
                frontmatter,
                response.data.images.jpg.large_image_url,
                response.data.url,
                response.data.score,
                response.data.synopsis
            )
            
            val updatedContent = writeFrontmatter(updatedFrontmatter, body)
            file.writeText(updatedContent)
            
            println("  ✓ Updated successfully")
            println("    - Image: ${response.data.images.jpg.large_image_url}")
            println("    - URL: ${response.data.url}")
            println("    - Score: ${response.data.score ?: "N/A"}")
            
            // Rate limiting: Jikan API allows 3 requests per second, 60 per minute
            // Being conservative with 1 request per second
            if (index < mdFiles.size - 1) {
                println("  ⏳ Waiting 1 second (rate limiting)...")
                delay(1000)
            }
            
        } catch (e: Exception) {
            println("  ✗ Error: ${e.message}")
            e.printStackTrace()
        }
        exitProcess(1)
    }
    
    client.close()
    println("\n✓ Processing complete!")
}
