package io.github.fugeekies.timeflies.ui.personal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.fugeekies.timeflies.domain.model.PersonalEvent
import io.github.fugeekies.timeflies.domain.model.PersonalEventError
import io.github.fugeekies.timeflies.domain.model.PersonalImportResult
import io.github.fugeekies.timeflies.domain.port.Clock
import io.github.fugeekies.timeflies.domain.usecase.PersonalEvents
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

/**
 * The event being added or changed in the dialog. The date is picked as three lists, so it may not exist yet
 * (31 February) or be incomplete.
 *
 * @property id null for a new event.
 */
data class PersonalDraft(
    val id: String? = null,
    val title: String = "",
    val day: Int? = null,
    val month: Month? = null,
    val year: Int? = null,
    val error: DraftError? = null,
) {
    /** The date picked, or null when a list is empty or the day does not exist in that month. */
    val date: LocalDate? get() {
        val d = day ?: return null
        val m = month ?: return null
        val y = year ?: return null
        return try {
            LocalDate(y, m.number, d)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    val complete: Boolean get() = day != null && month != null && year != null
}

/** What is wrong with the draft, shown under the fields. */
sealed interface DraftError {
    data class Rule(val error: PersonalEventError) : DraftError

    /** 31 February, 31 April… */
    data object DateDoesNotExist : DraftError
}

/** The last export or import, shown under the buttons until the next one. */
sealed interface TransferMessage {
    data object Exported : TransferMessage
    data object ExportFailed : TransferMessage
    data class Imported(val result: PersonalImportResult.Imported) : TransferMessage
    data object NotAnExport : TransferMessage
    data object ImportFailed : TransferMessage
}

data class PersonalEventsUiState(
    val today: LocalDate,
    val events: List<PersonalEvent>,
    /** The dialog is open on this draft; null when closed. */
    val draft: PersonalDraft? = null,
    val message: TransferMessage? = null,
    /** An export or import is running: the buttons wait. */
    val busy: Boolean = false,
)

/**
 * Settings › Mes événements (decision of 19/11/2026, docs/UISpec/Settings.template.md): the list of the user's own
 * events, the dialog to add or change one, the export and the import.
 *
 * @param files the system file picker; null where it does not exist (tests, previews): no export nor import then.
 */
class PersonalEventsViewModel(
    private val personalEvents: PersonalEvents,
    private val clock: Clock,
    private val files: FileExchange?,
) : ViewModel() {

    private val log = Logger.withTag("PersonalEvents")

    private data class Local(val draft: PersonalDraft? = null, val message: TransferMessage? = null, val busy: Boolean = false)

    private val local = MutableStateFlow(Local())

    val canTransfer: Boolean get() = files != null

    val state: StateFlow<PersonalEventsUiState?> = combine(personalEvents.observe(), local) { events, local ->
        PersonalEventsUiState(clock.today(), events, local.draft, local.message, local.busy)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onAdd() = local.update { it.copy(draft = PersonalDraft()) }

    fun onEdit(event: PersonalEvent) = local.update {
        it.copy(draft = PersonalDraft(event.id, event.title, event.date.day, event.date.month, event.date.year))
    }

    fun onDismiss() = local.update { it.copy(draft = null) }

    fun onDraftChange(transform: (PersonalDraft) -> PersonalDraft) = local.update { current ->
        // A change clears the error: it is checked again on saving.
        current.copy(draft = current.draft?.let(transform)?.copy(error = null))
    }

    fun onSave() {
        val draft = local.value.draft ?: return
        val date = draft.date
        if (date == null) {
            local.update { it.copy(draft = draft.copy(error = DraftError.DateDoesNotExist)) }
            return
        }
        viewModelScope.launch {
            val id = draft.id
            val error = if (id == null) personalEvents.add(draft.title, date) else personalEvents.update(id, draft.title, date)
            local.update { it.copy(draft = if (error == null) null else draft.copy(error = DraftError.Rule(error))) }
        }
    }

    fun onDelete() {
        val id = local.value.draft?.id ?: return
        viewModelScope.launch {
            personalEvents.remove(id)
            local.update { it.copy(draft = null) }
        }
    }

    fun onExport() {
        val files = files ?: return
        transfer(onFailure = TransferMessage.ExportFailed) {
            val saved = files.save(FileExchange.EXPORT_FILE_NAME, personalEvents.export())
            if (saved) TransferMessage.Exported else null
        }
    }

    fun onImport() {
        val files = files ?: return
        transfer(onFailure = TransferMessage.ImportFailed) {
            val text = files.open() ?: return@transfer null
            when (val result = personalEvents.import(text)) {
                is PersonalImportResult.Imported -> TransferMessage.Imported(result)
                PersonalImportResult.NotAnExport -> TransferMessage.NotAnExport
            }
        }
    }

    /** Runs an export or import, one at a time; a cancelled picker (null) leaves the previous message. */
    private fun transfer(onFailure: TransferMessage, block: suspend () -> TransferMessage?) {
        if (local.value.busy) return
        local.update { it.copy(busy = true) }
        viewModelScope.launch {
            val message = try {
                block()
            } catch (e: CancellationException) {
                local.update { it.copy(busy = false) }
                throw e
            } catch (e: Exception) {
                // The class only: the message may quote a path or the content of the file.
                log.w { "Export or import failed (${e::class.simpleName})" }
                onFailure
            }
            local.update { it.copy(busy = false, message = message ?: it.message) }
        }
    }
}
