package io.github.fugeekies.timeflies.android.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider as FixedColorProvider
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.android.MainActivity
import io.github.fugeekies.timeflies.android.TimeFliesApplication
import io.github.fugeekies.timeflies.domain.rules.Generations
import io.github.fugeekies.timeflies.ui.polaroid.Era
import io.github.fugeekies.timeflies.ui.text.WidgetContent
import io.github.fugeekies.timeflies.ui.text.WidgetSheet
import io.github.fugeekies.timeflies.ui.text.WidgetTexts
import io.github.fugeekies.timeflies.ui.theme.WidgetPalette
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

/**
 * The home screen widget (docs/UISpec/AndroidWidget.template.md): the same "coup de vieux" as the Today screen,
 * on a little Polaroïd, in three sizes. Its state keeps the sheet shown by the "next" button and a refresh counter.
 */
class TimeFliesWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(setOf(SMALL, WIDE, TALL))

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as TimeFliesApplication
        provideContent {
            val state = currentState<Preferences>()
            // Recomputed at each refresh (midnight, settings, debug panel), even while the widget session lives on.
            val content by produceState<Loaded?>(null, state[REFRESH] ?: 0) { value = load(context, app) }
            Widget(content, state[SHEET] ?: 0)
        }
    }

    /** The texts and the handwritten numbers of every sheet, so the "next" button only recomposes. */
    private class Loaded(val content: WidgetContent, val numbers: List<Bitmap>)

    private suspend fun load(context: Context, app: TimeFliesApplication): Loaded? = try {
        val graph = app.graph
        val settings = graph.observeSettings().first()
        val shock = graph.getDailyShock(settings.region, settings.birth?.let(Generations::userBirth))
        val content = WidgetTexts.of(shock, settings.onboardingDone)
        Loaded(content, content.sheets.map { Handwriting.bitmap(context, it.years, NUMBER_SIZE.value, WidgetPalette.of(false).ink) })
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Logger.withTag("Widget").e(e) { "Widget could not be computed" }
        null
    }

    @Composable
    private fun Widget(loaded: Loaded?, sheetIndex: Int) {
        val context = LocalContext.current
        val open = actionStartActivity(
            Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_SCREEN, MainActivity.SCREEN_TODAY)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
        )
        var modifier = GlanceModifier.fillMaxSize().appWidgetBackground().background(Colors.background).clickable(open).padding(12.dp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) modifier = modifier.cornerRadius(android.R.dimen.system_app_widget_background_radius)
        Box(modifier) {
            val content = loaded?.content
            when {
                content == null -> Unit
                content.sheets.isEmpty() -> Text(content.message.orEmpty(), style = TextStyle(color = Colors.onBackground, fontSize = 14.sp))
                else -> {
                    val index = sheetIndex.mod(content.sheets.size)
                    Sheet(content.sheets[index], loaded.numbers[index])
                    if (content.sheets.size > 1) NextButton(content.nextLabel)
                }
            }
        }
    }

    @Composable
    private fun Sheet(sheet: WidgetSheet, number: Bitmap) {
        val size = LocalSize.current
        if (size.width < WIDE.width) {
            // 2 × 2: the number and the title.
            Column(GlanceModifier.fillMaxSize()) {
                Polaroid(sheet, number)
                Spacer(GlanceModifier.height(6.dp))
                Text(sheet.title, maxLines = 2, style = TextStyle(color = Colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold))
            }
            return
        }
        Column(GlanceModifier.fillMaxSize()) {
            Text(sheet.eyebrow, maxLines = 1, style = TextStyle(color = Colors.secondary, fontSize = 11.sp, fontWeight = FontWeight.Bold))
            Spacer(GlanceModifier.height(6.dp))
            Row(GlanceModifier.fillMaxWidth()) {
                Polaroid(sheet, number)
                Spacer(GlanceModifier.width(12.dp))
                Column(GlanceModifier.defaultWeight()) {
                    Text(sheet.title, maxLines = 2, style = TextStyle(color = Colors.onBackground, fontSize = 15.sp, fontWeight = FontWeight.Bold))
                    Text(sheet.detail, maxLines = 1, style = TextStyle(color = Colors.secondary, fontSize = 12.sp))
                    sheet.userAge?.let { Text(it, maxLines = 1, style = TextStyle(color = Colors.accent, fontSize = 12.sp)) }
                }
            }
            if (size.height >= TALL.height) {
                Spacer(GlanceModifier.height(10.dp))
                sheet.compact.forEach { Text(it, maxLines = 1, style = TextStyle(color = Colors.secondary, fontSize = 12.sp)) }
            }
        }
    }

    /** The little Polaroïd: the frame in the colour of the decade, the years written by hand. */
    @Composable
    private fun Polaroid(sheet: WidgetSheet, number: Bitmap) {
        Box(
            GlanceModifier.background(FixedColorProvider(Era.of(sheet.eventYear).frame)).cornerRadius(4.dp).padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(ImageProvider(number), contentDescription = sheet.years)
        }
    }

    /** "›" in the bottom right corner: the next anniversary of the day, back to the first after the last. */
    @Composable
    private fun NextButton(label: String) {
        Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            Box(
                GlanceModifier.width(40.dp).height(40.dp).clickable(actionRunCallback<NextSheetAction>()).semantics { contentDescription = label },
                contentAlignment = Alignment.Center,
            ) {
                Text("›", style = TextStyle(color = Colors.accent, fontSize = 26.sp, fontWeight = FontWeight.Bold))
            }
        }
    }

    /** Colours of the Polaroïd style, light or dark following the system. */
    private object Colors {
        private val light = WidgetPalette.of(false)
        private val dark = WidgetPalette.of(true)
        val background = ColorProvider(light.background, dark.background)
        val onBackground = ColorProvider(light.onBackground, dark.onBackground)
        val secondary = ColorProvider(light.secondary, dark.secondary)
        val accent = ColorProvider(light.accent, dark.accent)
    }

    companion object {
        /** 2 × 2, 4 × 2 and 4 × 3: the richest layout that fits is chosen (decision of 13/11/2026). */
        val SMALL = DpSize(110.dp, 110.dp)
        val WIDE = DpSize(250.dp, 110.dp)
        val TALL = DpSize(250.dp, 180.dp)

        /** Size of the handwritten number, never under 20 sp like all handwriting (docs/UI_DESIGN_SPEC.md). */
        private val NUMBER_SIZE = 30.sp

        /** Sheet shown by the "next" button; it grows and is read modulo the number of sheets. */
        val SHEET = intPreferencesKey("sheet")

        /** Day of the sheets shown: on a new day the widget comes back to the main "coup de vieux". */
        val DAY = stringPreferencesKey("day")

        /** Bumped by each refresh, so a living widget session computes its content again. */
        val REFRESH = intPreferencesKey("refresh")
    }
}
