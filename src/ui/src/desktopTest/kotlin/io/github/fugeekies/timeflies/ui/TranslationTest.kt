package io.github.fugeekies.timeflies.ui

import io.github.fugeekies.timeflies.domain.model.AppLanguage
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The translations of the interface (milestone 17): every text of the French reference exists in every language of
 * [AppLanguage], with the same placeholders, and every list has the same length. The codes of the origins and
 * subcategories are keys, never translated.
 */
class TranslationTest {

    private val resources = File(System.getProperty("user.dir"), "src/commonMain/composeResources")

    /** A text: its kind, its name, and each of its strings (one per plural quantity or list item). */
    private data class Entry(val kind: String, val name: String, val values: List<String>)

    private fun read(folder: String): Map<String, Entry> {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(resources, "$folder/strings.xml"))
        val root = document.documentElement
        val entries = mutableMapOf<String, Entry>()
        for (i in 0 until root.childNodes.length) {
            val node = root.childNodes.item(i) as? Element ?: continue
            val values = when (node.tagName) {
                "string" -> listOf(node.textContent)
                else -> (0 until node.getElementsByTagName("item").length).map { node.getElementsByTagName("item").item(it).textContent }
            }
            entries[node.getAttribute("name")] = Entry(node.tagName, node.getAttribute("name"), values)
        }
        return entries
    }

    private val reference = read("values").filterKeys { !it.endsWith("_codes") }

    private fun placeholders(text: String): Set<String> =
        (Regex("""%\d+\$[sd]""").findAll(text) + Regex("""\{[a-z]+}""").findAll(text)).map { it.value }.toSet()

    @Test
    fun everyLanguageHasEveryText() {
        for (language in AppLanguage.entries.filter { it != AppLanguage.FRENCH }) {
            val translated = read("values-${language.tag}")
            val missing = reference.keys - translated.keys
            assertTrue(missing.isEmpty(), "values-${language.tag} lacks $missing")
            val extra = translated.keys - reference.keys
            assertTrue(extra.isEmpty(), "values-${language.tag} has texts the reference does not: $extra")
        }
    }

    @Test
    fun translationsKeepThePlaceholdersAndTheListLengths() {
        for (language in AppLanguage.entries.filter { it != AppLanguage.FRENCH }) {
            val translated = read("values-${language.tag}")
            for ((name, entry) in reference) {
                val other = translated[name] ?: continue
                assertEquals(entry.kind, other.kind, "${language.tag}/$name")
                if (entry.kind == "string-array") {
                    assertEquals(entry.values.size, other.values.size, "${language.tag}/$name: length")
                    continue
                }
                // The plural "one" may write the number out ("turns 1"): compared on the union of the quantities.
                val expected = entry.values.flatMap(::placeholders).toSet()
                val actual = other.values.flatMap(::placeholders).toSet()
                assertEquals(expected - "%1\$d", actual - "%1\$d", "${language.tag}/$name: placeholders")
            }
        }
    }
}
