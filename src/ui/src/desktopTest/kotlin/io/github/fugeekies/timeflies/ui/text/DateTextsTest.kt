package io.github.fugeekies.timeflies.ui.text

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/** Dates in the order of each language (milestone 17), with the patterns of the string resources. */
class DateTextsTest {

    private val day = LocalDate(2026, 11, 2)
    private val first = LocalDate(1991, 5, 1)

    private fun months(vararg names: String) = names.toList()

    private val french = DateTexts(
        months("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre"),
        listOf("lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"),
        firstOfMonth = "1er",
    )

    @Test
    fun writesTheFrenchReference() {
        assertEquals("2 novembre 2026", french.long(day))
        assertEquals("1er mai 1991", french.long(first))
        assertEquals("lundi 2 novembre 2026", french.withDayName(day))
        assertEquals("lundi 2 novembre", french.dayAndMonth(day))
        assertEquals("02/11/2026", french.short(day))
    }

    @Test
    fun followsTheOrderOfEachLanguage() {
        val german = DateTexts(
            months("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"),
            listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"),
            firstOfMonth = "1",
            patterns = DatePatterns(long = "{d}. {m} {y}", dayAndMonth = "{w}, {d}. {m}", withDayName = "{w}, {date}", short = "{dd}.{mm}.{y}"),
        )
        assertEquals("2. November 2026", german.long(day))
        assertEquals("Montag, 2. November 2026", german.withDayName(day))
        assertEquals("Montag, 2. November", german.dayAndMonth(day))
        assertEquals("02.11.2026", german.short(day))

        val spanish = DateTexts(
            months("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"),
            listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"),
            firstOfMonth = "1",
            patterns = DatePatterns(long = "{d} de {m} de {y}", dayAndMonth = "{w}, {d} de {m}", withDayName = "{w}, {date}", short = "{dd}/{mm}/{y}"),
        )
        assertEquals("1 de mayo de 1991", spanish.long(first))
        assertEquals("lunes, 2 de noviembre de 2026", spanish.withDayName(day))
    }
}
