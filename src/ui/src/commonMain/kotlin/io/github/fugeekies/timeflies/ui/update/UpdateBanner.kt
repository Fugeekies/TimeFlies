package io.github.fugeekies.timeflies.ui.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.fugeekies.timeflies.domain.model.AppRelease
import io.github.fugeekies.timeflies.domain.usecase.ReleaseUpdates
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.update_available
import io.github.fugeekies.timeflies.ui.resources.update_download
import io.github.fugeekies.timeflies.ui.resources.update_later
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/** The version offered by the banner on Today, or null (decision of 16/11/2026). */
class UpdateViewModel(private val releaseUpdates: ReleaseUpdates) : ViewModel() {
    val release: StateFlow<AppRelease?> = releaseUpdates.banner()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun later(release: AppRelease) {
        viewModelScope.launch { releaseUpdates.dismiss(release.version) }
    }
}

/**
 * "Nouvelle version : TimeFlies 0.2.0", with "Télécharger" (the release page in the browser) and "Plus tard" (this
 * version does not come back). Nothing is shown without a newer version.
 */
@Composable
fun UpdateBanner(viewModel: UpdateViewModel, modifier: Modifier = Modifier) {
    val release by viewModel.release.collectAsStateWithLifecycle()
    val current = release ?: return
    val uriHandler = LocalUriHandler.current
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Column(Modifier.padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 4.dp)) {
            Text(stringResource(Res.string.update_available, current.version), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { viewModel.later(current) }) { Text(stringResource(Res.string.update_later)) }
                TextButton(onClick = { uriHandler.openUri(current.url) }) { Text(stringResource(Res.string.update_download)) }
            }
        }
    }
}
