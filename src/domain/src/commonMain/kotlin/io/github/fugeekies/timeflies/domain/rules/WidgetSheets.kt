package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.Anniversary
import io.github.fugeekies.timeflies.domain.model.DailyShock

/**
 * What the Android widget goes through (docs/UISpec/AndroidWidget.template.md, "Interaction"): the "next" button
 * shows the following anniversary of the day, in the usual order, then comes back to the first.
 */
object WidgetSheets {
    /**
     * The anniversaries of the main sheet's day, in display order: the main one, then the others of the same day
     * ("Aussi aujourd'hui", or the other anniversaries of the upcoming day shown). Empty when there is none.
     */
    fun of(shock: DailyShock): List<Anniversary> {
        val main = shock.main ?: return emptyList()
        val others = if (shock.mainInDays == 0) {
            shock.alsoToday
        } else {
            shock.upcoming.firstOrNull { it.date == main.day }?.anniversaries.orEmpty()
        }
        return listOf(main) + others
    }

    /**
     * The [count] anniversaries after sheet [index] (4 × 3 and larger): the next ones of the day, then those of the
     * following days, so the compact lines follow the "next" button.
     */
    fun following(shock: DailyShock, index: Int, count: Int = 2): List<Anniversary> {
        val sheets = of(shock)
        if (sheets.isEmpty()) return emptyList()
        val later = shock.upcoming.filter { day -> sheets.none { it.day == day.date } }.flatMap { it.anniversaries }
        return (sheets + later).drop(index.coerceIn(sheets.indices) + 1).take(count)
    }

    /** The sheet after [index], back to the first after the last. */
    fun next(index: Int, size: Int): Int = if (size <= 1) 0 else (index + 1).mod(size)
}
