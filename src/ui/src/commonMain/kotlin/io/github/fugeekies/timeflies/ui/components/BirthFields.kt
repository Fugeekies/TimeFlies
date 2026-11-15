package io.github.fugeekies.timeflies.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.settings_month
import io.github.fugeekies.timeflies.ui.resources.settings_year
import io.github.fugeekies.timeflies.ui.text.capitalized
import kotlinx.datetime.Month
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/**
 * The two lists "Mois" and "Année" of the birth, shared by the first launch and Settings, never the day
 * (decision of 13/11/2026). Years are offered from the most recent.
 */
@Composable
fun BirthFields(
    month: Month?,
    year: Int?,
    years: IntRange,
    onMonthPicked: (Month) -> Unit,
    onYearPicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val months = stringArrayResource(Res.array.months).map { it.capitalized() }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ChoiceField(
            name = stringResource(Res.string.settings_month),
            value = month?.let { months[it.number - 1] },
            options = Month.entries,
            optionLabel = { months[it.number - 1] },
            onPick = onMonthPicked,
            modifier = Modifier.weight(1f),
        )
        ChoiceField(
            name = stringResource(Res.string.settings_year),
            value = year?.toString(),
            options = years.reversed().toList(),
            optionLabel = { it.toString() },
            onPick = onYearPicked,
            modifier = Modifier.weight(1f),
        )
    }
}

/** A button that opens a list of choices: [value] once picked, otherwise the field [name]. */
@Composable
private fun <T> ChoiceField(
    name: String,
    value: String?,
    options: List<T>,
    optionLabel: (T) -> String,
    onPick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = if (value == null) name else "$name : $value" },
        ) {
            Text(value ?: name, modifier = Modifier.weight(1f), color = if (value == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
            Text("▾", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.heightIn(max = 320.dp)) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onPick(option)
                    },
                )
            }
        }
    }
}
