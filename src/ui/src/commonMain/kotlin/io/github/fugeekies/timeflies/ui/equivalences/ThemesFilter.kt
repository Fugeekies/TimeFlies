package io.github.fugeekies.timeflies.ui.equivalences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.equivalences_check_all
import io.github.fugeekies.timeflies.ui.resources.equivalences_themes
import io.github.fugeekies.timeflies.ui.resources.equivalences_themes_summary
import io.github.fugeekies.timeflies.ui.resources.equivalences_uncheck_all
import io.github.fugeekies.timeflies.ui.resources.ok
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The "Thèmes" button, how many are ticked, and the dialog to tick them. Shared by Equivalences and "Quel âge ?", which
 * share the same saved choice too (decision of 18/11/2026, docs/UISpec/Equivalences.template.md, "Filtres").
 */
@Composable
internal fun ThemesFilter(
    checked: Set<Family>,
    onFamily: (Family, Boolean) -> Unit,
    onAll: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = { open = true }) { Text(stringResource(Res.string.equivalences_themes)) }
        Text(
            pluralStringResource(Res.plurals.equivalences_themes_summary, checked.size, checked.size, Family.entries.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    if (open) ThemesDialog(checked, onFamily, onAll, onDismiss = { open = false })
}

/** The themes as check boxes, saved at once. */
@Composable
private fun ThemesDialog(checked: Set<Family>, onFamily: (Family, Boolean) -> Unit, onAll: (Boolean) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.ok)) } },
        title = { Text(stringResource(Res.string.equivalences_themes)) },
        text = {
            Column {
                Family.entries.forEach { family ->
                    val isChecked = family in checked
                    Row(
                        Modifier.fillMaxWidth().clickable(role = Role.Checkbox) { onFamily(family, !isChecked) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(checked = isChecked, onCheckedChange = null)
                        Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(family), CircleShape))
                        Text(familyLabel(family), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onAll(true) }) { Text(stringResource(Res.string.equivalences_check_all)) }
                    TextButton(onClick = { onAll(false) }) { Text(stringResource(Res.string.equivalences_uncheck_all)) }
                }
            }
        },
    )
}
