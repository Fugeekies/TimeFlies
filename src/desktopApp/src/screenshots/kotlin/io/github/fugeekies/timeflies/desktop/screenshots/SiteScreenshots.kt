package io.github.fugeekies.timeflies.desktop.screenshots

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.Density
import io.github.fugeekies.timeflies.data.events.JsonEventRepository
import io.github.fugeekies.timeflies.domain.model.AppLanguage
import io.github.fugeekies.timeflies.domain.model.DisplayStyle
import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.model.Settings
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.port.PersonalEventRepository
import io.github.fugeekies.timeflies.domain.port.SettingsRepository
import io.github.fugeekies.timeflies.domain.usecase.CompleteOnboarding
import io.github.fugeekies.timeflies.domain.usecase.CountEvents
import io.github.fugeekies.timeflies.domain.usecase.GetDailyShock
import io.github.fugeekies.timeflies.domain.usecase.GetEquivalences
import io.github.fugeekies.timeflies.domain.usecase.GetEventAges
import io.github.fugeekies.timeflies.domain.usecase.GetEventGroups
import io.github.fugeekies.timeflies.domain.usecase.ObserveSettings
import io.github.fugeekies.timeflies.domain.usecase.PersonalEvents
import io.github.fugeekies.timeflies.domain.usecase.SearchEvents
import io.github.fugeekies.timeflies.domain.usecase.UpdateSettings
import io.github.fugeekies.timeflies.ui.App
import io.github.fugeekies.timeflies.ui.AppUseCases
import io.github.fugeekies.timeflies.ui.readResourceFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import java.io.File
import javax.imageio.ImageIO

/**
 * Screenshots of the presentation page (site/, docs/DEVELOPMENT.md, "Page de présentation"): the real screens,
 * rendered off screen in English with the real event base, on a fixed date and for a sample birth (May 1991, as in
 * the mockups), so they can be made again after a change of the screens.
 *
 * `./gradlew :desktopApp:siteScreenshots` writes them to site/assets/.
 */
fun main(args: Array<String>) {
    val output = File(args.single()).apply { mkdirs() }
    // A phone (Pixel-like, 412 × 892 dp) and a Windows window (1000 × 700 dp, the app's default size), at 2.5 and
    // 1.5 times so the text stays sharp on high-density screens.
    val phone = Frame(412, 892, 2.5f)
    val window = Frame(1000, 700, 1.5f)
    session(output, phone, DisplayStyle.POLAROID) {
        capture("today.png")
        // The Polaroïd opens "How old?" on its event; a 15-year-old is added, as the app suggests.
        tap(TODAY_EVENT, substring = true)
        tap("Add an age")
        tap("OK")
        capture("how-old.png")
        // The added age is shared with Equivalences.
        tap("Equivalences")
        capture("equivalences.png")
    }
    session(output, phone, DisplayStyle.VHS) { capture("today-vhs.png") }
    session(output, phone, DisplayStyle.EPHEMERIS) { capture("today-ephemeris.png") }
    session(output, window, DisplayStyle.POLAROID) { capture("windows.png") }
}

/** The size of a screenshot in dp and its density. */
private class Frame(val widthDp: Int, val heightDp: Int, val density: Float)

/** The fixed date of the screenshots: [TODAY_EVENT] came out in France 25 years earlier. */
private val TODAY = LocalDate(2026, 12, 5)
private const val TODAY_EVENT = "Harry Potter and the Philosopher"

/** Settings kept in memory: nothing is read from or written to the user's own %APPDATA%\TimeFlies. */
private class MemorySettings(initial: Settings) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val settings = state
    override suspend fun update(transform: (Settings) -> Settings) = state.update(transform)
}

/** No personal events on the screenshots: only the real base (and nothing read from the user's own files). */
private object NoPersonalEvents : PersonalEventRepository {
    override val events = MutableStateFlow(emptyList<PersonalEvent>())
    override suspend fun add(title: String, date: LocalDate) = Unit
    override suspend fun update(id: String, title: String, date: LocalDate) = Unit
    override suspend fun remove(id: String) = Unit
    override suspend fun addAll(events: List<Pair<String, LocalDate>>) = Unit
    override suspend fun encode() = ""
    override fun decode(text: String): PersonalEventRepository.Decoded? = null
}

/** One launch of the app, through which [Session] taps and captures. */
@OptIn(ExperimentalTestApi::class)
private class Session(private val test: ComposeUiTest, private val output: File) {
    /** Taps the node showing [text]; [substring] also finds text drawn inside a card that hides it from the merged tree. */
    fun tap(text: String, substring: Boolean = false) {
        test.onNodeWithText(text, substring = substring, useUnmergedTree = substring).performClick()
        test.settle()
    }

    fun capture(name: String) {
        val file = output.resolve(name)
        val image = test.onRoot().captureToImage().toAwtImage()
        ImageIO.write(image, "png", file)
        println("$file (${image.width} x ${image.height})")
    }
}

@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class, InternalComposeUiApi::class)
private fun session(output: File, frame: Frame, style: DisplayStyle, steps: Session.() -> Unit) {
    val clock = Clock { TODAY }
    val settings = MemorySettings(
        Settings(birth = YearMonth(1991, 5), onboardingDone = true, displayStyle = style, language = AppLanguage.ENGLISH),
    )
    val events = JsonEventRepository(readFile = ::readResourceFile, clock = clock, excludedGroups = { settings.settings.first().excludedEventGroups })
    runDesktopComposeUiTest(width = (frame.widthDp * frame.density).toInt(), height = (frame.heightDp * frame.density).toInt()) {
        setContent {
            // Light, whatever the theme of the PC that renders them (the VHS stays dark).
            CompositionLocalProvider(LocalDensity provides Density(frame.density), LocalSystemTheme provides SystemTheme.Light) {
                App(
                    AppUseCases(
                        getDailyShock = GetDailyShock(events, clock),
                        getEquivalences = GetEquivalences(events, clock),
                        getEventAges = GetEventAges(),
                        searchEvents = SearchEvents(events, clock),
                        observeSettings = ObserveSettings(settings),
                        updateSettings = UpdateSettings(settings, clock),
                        completeOnboarding = CompleteOnboarding(settings, clock),
                        countEvents = CountEvents(events),
                        getEventGroups = GetEventGroups(events),
                        personalEvents = PersonalEvents(NoPersonalEvents, clock),
                        clock = clock,
                    ),
                )
            }
        }
        settle()
        Session(this, output).steps()
    }
}

/** Waits for the event base, the fonts and the drawings, which load off the main thread. */
@OptIn(ExperimentalTestApi::class)
private fun ComposeUiTest.settle() {
    repeat(20) {
        waitForIdle()
        Thread.sleep(100)
    }
}
