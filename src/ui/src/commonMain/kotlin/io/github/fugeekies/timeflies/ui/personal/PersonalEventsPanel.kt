package io.github.fugeekies.timeflies.ui.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.model.PersonalEventError
import io.github.fugeekies.timeflies.ui.components.ChoiceField
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.cancel
import io.github.fugeekies.timeflies.ui.resources.months
import io.github.fugeekies.timeflies.ui.resources.personal_add
import io.github.fugeekies.timeflies.ui.resources.personal_day
import io.github.fugeekies.timeflies.ui.resources.personal_delete
import io.github.fugeekies.timeflies.ui.resources.personal_edit
import io.github.fugeekies.timeflies.ui.resources.personal_error_date_future
import io.github.fugeekies.timeflies.ui.resources.personal_error_date_invalid
import io.github.fugeekies.timeflies.ui.resources.personal_error_title_missing
import io.github.fugeekies.timeflies.ui.resources.personal_error_title_too_long
import io.github.fugeekies.timeflies.ui.resources.personal_export
import io.github.fugeekies.timeflies.ui.resources.personal_export_done
import io.github.fugeekies.timeflies.ui.resources.personal_export_failed
import io.github.fugeekies.timeflies.ui.resources.personal_import
import io.github.fugeekies.timeflies.ui.resources.personal_import_added
import io.github.fugeekies.timeflies.ui.resources.personal_import_duplicates
import io.github.fugeekies.timeflies.ui.resources.personal_import_failed
import io.github.fugeekies.timeflies.ui.resources.personal_import_invalid
import io.github.fugeekies.timeflies.ui.resources.personal_import_not_export
import io.github.fugeekies.timeflies.ui.resources.personal_new
import io.github.fugeekies.timeflies.ui.resources.personal_save
import io.github.fugeekies.timeflies.ui.resources.personal_title
import io.github.fugeekies.timeflies.ui.resources.personal_title_hint
import io.github.fugeekies.timeflies.ui.resources.personal_transfer_help
import io.github.fugeekies.timeflies.ui.resources.settings_month
import io.github.fugeekies.timeflies.ui.resources.settings_personal_empty
import io.github.fugeekies.timeflies.ui.resources.settings_personal_help
import io.github.fugeekies.timeflies.ui.resources.settings_year
import io.github.fugeekies.timeflies.ui.text.capitalized
import io.github.fugeekies.timeflies.ui.text.rememberDateTexts
import io.github.fugeekies.timeflies.ui.theme.LocalStyle
import kotlinx.datetime.Month
import kotlinx.datetime.number
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/** The oldest year offered: the app counts years within a life, a few generations back at most. */
private const val FIRST_YEAR = 1900

/**
 * Settings › Mes événements (decision of 19/11/2026, docs/UISpec/Settings.template.md): the user's own events, touched
 * to change or delete one, the button to add one, then the export and the import to change phones.
 */
@Composable
fun ColumnScope.PersonalEventsPanel(viewModel: PersonalEventsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state ?: return
    val colors = MaterialTheme.colorScheme
    val dates = rememberDateTexts()

    Text(stringResource(Res.string.settings_personal_help), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
    if (current.events.isEmpty()) {
        Text(stringResource(Res.string.settings_personal_empty), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
    } else {
        Column {
            current.events.forEach { event -> EventRow(event, dates.long(event.date)) { viewModel.onEdit(event) } }
        }
    }
    Button(onClick = viewModel::onAdd, modifier = Modifier.align(Alignment.End)) { Text(stringResource(Res.string.personal_add)) }

    if (viewModel.canTransfer) {
        Text(stringResource(Res.string.personal_transfer_help), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Row(Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = viewModel::onImport, enabled = !current.busy) { Text(stringResource(Res.string.personal_import)) }
            OutlinedButton(onClick = viewModel::onExport, enabled = !current.busy && current.events.isNotEmpty()) {
                Text(stringResource(Res.string.personal_export))
            }
        }
        current.message?.let { Text(messageText(it), style = MaterialTheme.typography.bodyMedium, color = colors.primary) }
    }

    current.draft?.let { draft -> DraftDialog(draft, current.today.year, viewModel) }
}

/** The family dot, the title and the date; the whole row opens the dialog. */
@Composable
private fun EventRow(event: PersonalEvent, date: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 10.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(10.dp).background(LocalStyle.current.familyColors.getValue(Family.PERSONAL), CircleShape))
        Column(Modifier.weight(1f)) {
            Text(event.title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Add or change an event: title, then day, month and year as lists, as the birth is picked. */
@Composable
private fun DraftDialog(draft: PersonalDraft, lastYear: Int, viewModel: PersonalEventsViewModel) {
    val months = stringArrayResource(Res.array.months).map { it.capitalized() }
    AlertDialog(
        onDismissRequest = viewModel::onDismiss,
        title = { Text(stringResource(if (draft.id == null) Res.string.personal_new else Res.string.personal_edit)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = draft.title,
                    onValueChange = { text -> viewModel.onDraftChange { it.copy(title = text.take(PersonalEvent.MAX_TITLE_LENGTH)) } },
                    label = { Text(stringResource(Res.string.personal_title)) },
                    placeholder = { Text(stringResource(Res.string.personal_title_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoiceField(
                        name = stringResource(Res.string.personal_day),
                        value = draft.day?.toString(),
                        options = (1..31).toList(),
                        optionLabel = { it.toString() },
                        onPick = { day -> viewModel.onDraftChange { it.copy(day = day) } },
                        modifier = Modifier.weight(0.8f),
                    )
                    ChoiceField(
                        name = stringResource(Res.string.settings_month),
                        value = draft.month?.let { months[it.number - 1] },
                        options = Month.entries,
                        optionLabel = { months[it.number - 1] },
                        onPick = { month -> viewModel.onDraftChange { it.copy(month = month) } },
                        modifier = Modifier.weight(1.4f),
                    )
                    ChoiceField(
                        name = stringResource(Res.string.settings_year),
                        value = draft.year?.toString(),
                        options = (FIRST_YEAR..lastYear).reversed().toList(),
                        optionLabel = { it.toString() },
                        onPick = { year -> viewModel.onDraftChange { it.copy(year = year) } },
                        modifier = Modifier.weight(1f),
                    )
                }
                draft.error?.let { Text(errorText(it), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error) }
                if (draft.id != null) {
                    TextButton(onClick = viewModel::onDelete) { Text(stringResource(Res.string.personal_delete), color = MaterialTheme.colorScheme.error) }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::onSave, enabled = draft.title.isNotBlank() && draft.complete) { Text(stringResource(Res.string.personal_save)) }
        },
        dismissButton = { TextButton(onClick = viewModel::onDismiss) { Text(stringResource(Res.string.cancel)) } },
    )
}

@Composable
private fun errorText(error: DraftError): String = when (error) {
    DraftError.DateDoesNotExist -> stringResource(Res.string.personal_error_date_invalid)
    is DraftError.Rule -> when (error.error) {
        PersonalEventError.TITLE_MISSING -> stringResource(Res.string.personal_error_title_missing)
        PersonalEventError.TITLE_TOO_LONG -> stringResource(Res.string.personal_error_title_too_long, PersonalEvent.MAX_TITLE_LENGTH)
        PersonalEventError.DATE_IN_FUTURE -> stringResource(Res.string.personal_error_date_future)
    }
}

/** "3 événements ajoutés. 1 déjà présent, ignoré.": the counts that are not zero. */
@Composable
private fun messageText(message: TransferMessage): String = when (message) {
    TransferMessage.Exported -> stringResource(Res.string.personal_export_done)
    TransferMessage.ExportFailed -> stringResource(Res.string.personal_export_failed)
    TransferMessage.ImportFailed -> stringResource(Res.string.personal_import_failed)
    TransferMessage.NotAnExport -> stringResource(Res.string.personal_import_not_export)
    is TransferMessage.Imported -> {
        val result = message.result
        listOfNotNull(
            pluralStringResource(Res.plurals.personal_import_added, result.added, result.added),
            if (result.duplicates > 0) pluralStringResource(Res.plurals.personal_import_duplicates, result.duplicates, result.duplicates) else null,
            if (result.invalid > 0) pluralStringResource(Res.plurals.personal_import_invalid, result.invalid, result.invalid) else null,
        ).joinToString(" ")
    }
}
