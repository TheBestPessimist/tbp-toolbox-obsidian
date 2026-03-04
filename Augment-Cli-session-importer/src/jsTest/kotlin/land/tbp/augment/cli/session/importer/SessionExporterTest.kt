package land.tbp.augment.cli.session.importer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import node.buffer.BufferEncoding
import node.buffer.utf8
import node.fs.readFileSync
import node.fs.writeFileSync

/**
 * In Kotlin JS, test resources from `src/jsTest/resources` are copied to
 * `build/js/packages/<package-name>/kotlin/` but tests run from
 * `build/js/packages/<package-name>/`. This differs from JVM where
 * resources are on the classpath. We use this prefix to locate them.
 */
private const val RESOURCES_PATH = "kotlin/"

/**
 * Tests for SessionExporter markdown generation.
 * Uses fixture files to track formatting changes over time.
 */
class SessionExporterTest : FunSpec(
    {

        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        test("should export session 1e103837 to expected markdown format") {
            // Load the session fixture
            val sessionJson = readFileSync(
                "${RESOURCES_PATH}fixtures/1e103837-ea1a-4224-bf80-9209d68e8142.json",
                BufferEncoding.utf8,
            )
            val session = json.decodeFromString<Session>(sessionJson)

            // Export to markdown
            val actualMarkdown = session.toMarkdown()

            // Load expected output
            val expectedMarkdown = readFileSync(
                "${RESOURCES_PATH}fixtures/expected-1e103837.md",
                BufferEncoding.utf8,
            )

            // Compare line by line to find first difference
            val actualLines = actualMarkdown.trim().lines()
            val expectedLines = expectedMarkdown.trim().lines()

            actualLines.zip(expectedLines).forEach { (actual, expected) ->
                actual shouldBeEqualComparingTo expected
            }
        }
    },
)
