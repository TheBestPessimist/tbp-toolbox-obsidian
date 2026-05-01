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
import kotlin.time.Duration.Companion.milliseconds

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


fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    val directory = File("ZZZZZZZZZZZ")
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
                delay(1000.milliseconds)
            }
            
        } catch (e: Exception) {
            println("  ✗ Error: ${e.message}")
            e.printStackTrace()
            // exitProcess(1)
        }
    }
    
    client.close()
    println("\n✓ Processing complete!")
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
