package io.github.fugeekies.timeflies.data.debug

import io.github.fugeekies.timeflies.data.events.CoverageReport
import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.data.time.AdjustableClock
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.BaseStats
import io.github.fugeekies.timeflies.domain.port.DebugTools
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

/** The debug panel's tools on top of the real data layer. Built by the apps in debug builds only. */
class DataDebugTools(
    private val clock: AdjustableClock,
    private val settings: SettingsRepository,
    private val events: JsonEventRepository,
) : DebugTools {

    override val fakeDate: StateFlow<LocalDate?> = clock.fakeDate

    override fun setFakeDate(date: LocalDate?) = clock.set(date)

    override suspend fun forceSettings(transform: (Settings) -> Settings) = settings.update(transform)

    override suspend fun baseStats(): BaseStats {
        val report = events.load()
        val coverage = CoverageReport.of(report.events, settings.settings.first().region)
        return BaseStats(
            eventCount = coverage.eventCount,
            eventsByFamily = coverage.eventsByFamily,
            emptyDays = coverage.emptyDays,
            minEventsPerType = CoverageReport.MIN_EVENTS_PER_TYPE,
            typesBelowMinimum = coverage.typesBelowMinimum,
            rejections = report.rejections.map { it.toString() },
        )
    }
}
