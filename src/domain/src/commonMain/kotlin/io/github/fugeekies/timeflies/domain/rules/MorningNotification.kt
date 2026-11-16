package io.github.fugeekies.timeflies.domain.rules

import io.github.fugeekies.timeflies.domain.model.WindowsSettings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * When the Windows morning notification is sent (docs/UISpec/WindowsTray.template.md): each day at the chosen time,
 * or at the first wake-up of the day after it when the PC was off or asleep, and only once a day.
 */
object MorningNotification {
    /** Choices of the time list: every quarter of an hour, from 0:00 to 23:45. */
    val TIMES: List<LocalTime> = (0 until 24 * 4).map { LocalTime(it / 4, it % 4 * 15) }

    fun isQuarterHour(time: LocalTime): Boolean = time.minute % 15 == 0 && time.second == 0 && time.nanosecond == 0

    /** True when the notification of [today] is due at [now]: turned on, time reached, not sent yet today. */
    fun isDue(settings: WindowsSettings, today: LocalDate, now: LocalTime): Boolean =
        settings.morningNotification && now >= settings.notificationTime && settings.lastNotification != today
}
