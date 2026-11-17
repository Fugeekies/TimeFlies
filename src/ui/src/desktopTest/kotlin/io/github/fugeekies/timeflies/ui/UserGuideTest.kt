package io.github.fugeekies.timeflies.ui

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * The user guide in five languages (AGENTS.md, "Guide d'utilisation multilingue", decision of 17/11/2026): the same
 * pages in every language, each starting with the language selector towards the same page, and no broken link or
 * anchor. French is the reference.
 */
class UserGuideTest {

    private val languages = listOf("en" to "English", "fr" to "Français", "de" to "Deutsch", "it" to "Italiano", "es" to "Español")

    /** The repository root: the first folder above the module that holds AGENTS.md. */
    private val root: File = generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
        .firstOrNull { File(it, "AGENTS.md").isFile }
        ?: fail("AGENTS.md not found above ${System.getProperty("user.dir")}")

    private val guide = File(root, "docs/guide")

    private fun pages(language: String): Set<String> =
        File(guide, language).listFiles { file -> file.extension == "md" }.orEmpty().map { it.name }.toSet()

    @Test
    fun everyLanguageHasTheSamePagesAsFrench() {
        val reference = pages("fr")
        assertTrue("README.md" in reference, "the French guide has a contents page")
        for ((language, _) in languages) assertEquals(reference, pages(language), "pages of docs/guide/$language")
    }

    @Test
    fun everyPageStartsWithTheLanguageSelector() {
        for ((language, _) in languages) {
            for (page in pages(language)) {
                val firstLine = File(guide, "$language/$page").useLines { it.first() }
                for ((other, name) in languages) {
                    val expected = if (other == language) "**$name**" else "[$name](../$other/$page)"
                    assertTrue(expected in firstLine, "$language/$page: the selector lacks $expected")
                }
                if (page != "README.md") assertTrue("(README.md)" in firstLine, "$language/$page: no link back to the contents")
            }
        }
    }

    @Test
    fun linksAndAnchorsLeadSomewhere() {
        val files = languages.flatMap { (language, _) -> pages(language).map { File(guide, "$language/$it") } } + File(root, "README.md")
        for (file in files) {
            for (target in LINK.findAll(file.readText()).map { it.groupValues[1] }) {
                if (target.startsWith("http") || target.startsWith("#") && file.name == "README.md" && file.parentFile == root) continue
                val path = target.substringBefore('#')
                val anchor = target.substringAfter('#', "")
                val linked = if (path.isEmpty()) file else File(file.parentFile, path).normalize()
                assertTrue(linked.exists(), "${file.relativeTo(root)}: broken link $target")
                if (anchor.isNotEmpty() && linked.extension == "md") {
                    val slugs = linked.readLines().filter { it.startsWith("#") }.map { slug(it.trimStart('#').trim()) }
                    assertTrue(anchor in slugs, "${file.relativeTo(root)}: no heading for #$anchor in ${linked.relativeTo(root)}")
                }
            }
        }
    }

    @Test
    fun theReadmeLeadsToEveryGuide() {
        val readme = File(root, "README.md").readText()
        for ((language, name) in languages) {
            assertTrue("docs/guide/$language/README.md" in readme, "README.md: no link to the $language guide")
            assertTrue("[$name](#${slug(name)})" in readme, "README.md: no $name entry in the language list")
        }
    }

    private companion object {
        /** Markdown links: `[text](target)`, images included. */
        val LINK = Regex("""\]\(([^)\s]+)\)""")

        /** The anchor GitHub gives a heading: lower case, punctuation dropped, spaces as hyphens. */
        fun slug(heading: String): String =
            heading.lowercase().filter { it.isLetterOrDigit() || it == ' ' || it == '-' }.replace(' ', '-')
    }
}
