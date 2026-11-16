package io.github.fugeekies.timeflies.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.selection.triStateToggleable
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import io.github.fugeekies.timeflies.ui.resources.Res
import io.github.fugeekies.timeflies.ui.resources.event_count
import io.github.fugeekies.timeflies.ui.resources.origin_codes
import io.github.fugeekies.timeflies.ui.resources.origin_names
import io.github.fugeekies.timeflies.ui.resources.settings_events_collapse
import io.github.fugeekies.timeflies.ui.resources.settings_events_expand
import io.github.fugeekies.timeflies.ui.resources.subcategory_codes
import io.github.fugeekies.timeflies.ui.resources.subcategory_names
import io.github.fugeekies.timeflies.ui.text.familyLabel
import io.github.fugeekies.timeflies.ui.text.typeLabel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/**
 * A node of the event filter (decision of 16/11/2026): a family, a type of it, an origin of that type or a
 * subcategory of that origin, with the groups (files) under it.
 *
 * @property key the path of the node ("music", "music/rap-album-hit", "music/rap-album-hit/us"…).
 * @property leaves paths of the groups under this node: ticking or unticking the node does it for all of them.
 */
data class FilterNode(
    val key: String,
    val family: Family,
    val type: EventType?,
    val origin: String?,
    val subcategory: String?,
    val leaves: Set<String>,
    val count: Int,
    val children: List<FilterNode>,
) {
    /** Ticked when none of its groups is excluded, unticked when all are, partly otherwise. */
    fun state(excluded: Set<String>): ToggleableState {
        val out = leaves.count { it in excluded }
        return when (out) {
            0 -> ToggleableState.On
            leaves.size -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }

    /** The excluded groups after a touch: a ticked node is unticked with all its groups, any other one is ticked. */
    fun toggle(excluded: Set<String>): Set<String> =
        if (state(excluded) == ToggleableState.On) excluded + leaves else excluded - leaves
}

/** The tree family › type › origin › subcategory of the groups of the base, in the order of the families. */
fun filterTree(groups: List<EventGroupCount>): List<FilterNode> =
    groups.groupBy { it.group.family }.toSortedMap().map { (family, ofFamily) ->
        val types = ofFamily.groupBy { it.group.type }.toSortedMap().map { (type, ofType) ->
            val origins = ofType.groupBy { it.group.origin }.toSortedMap().map { (origin, ofOrigin) ->
                val subcategories = ofOrigin.filter { it.group.subcategory != null }.sortedBy { it.group.subcategory }.map {
                    FilterNode(it.group.path, family, type, origin, it.group.subcategory, setOf(it.group.path), it.count, emptyList())
                }
                node("${family.id}/${type.id}/$origin", family, type, origin, ofOrigin, subcategories)
            }
            node("${family.id}/${type.id}", family, type, null, ofType, origins)
        }
        node(family.id, family, null, null, ofFamily, types)
    }

private fun node(key: String, family: Family, type: EventType?, origin: String?, groups: List<EventGroupCount>, children: List<FilterNode>) =
    FilterNode(key, family, type, origin, null, groups.mapTo(LinkedHashSet()) { it.group.path }, groups.sumOf { it.count }, children)

/** "France", "États-Unis"… from the string resources; the code in capitals when it has no name yet. */
@Composable
fun rememberOriginNames(): (String) -> String {
    val names = stringArrayResource(Res.array.origin_codes).zip(stringArrayResource(Res.array.origin_names)).toMap()
    return { code -> names[code] ?: code.uppercase() }
}

@Composable
private fun rememberSubcategoryNames(): (String) -> String {
    val names = stringArrayResource(Res.array.subcategory_codes).zip(stringArrayResource(Res.array.subcategory_names)).toMap()
    return { code -> names[code] ?: code.replaceFirstChar { it.titlecase() } }
}

/**
 * The tree of the event filter, families first, each level unfolding on demand. A touch on a box ticks or unticks
 * everything under it; [onChange] receives the new set of excluded groups.
 */
@Composable
fun EventFilterTree(nodes: List<FilterNode>, excluded: Set<String>, onChange: (Set<String>) -> Unit, modifier: Modifier = Modifier) {
    val originName = rememberOriginNames()
    val subcategoryName = rememberSubcategoryNames()
    var expanded by rememberSaveable { mutableStateOf(emptySet<String>()) }
    Column(modifier) {
        fun Collection<FilterNode>.rows(depth: Int): List<Pair<FilterNode, Int>> = flatMap { node ->
            listOf(node to depth) + if (node.key in expanded) node.children.rows(depth + 1) else emptyList()
        }
        nodes.rows(0).forEach { (node, depth) ->
            val label = when {
                node.subcategory != null -> subcategoryName(node.subcategory)
                node.origin != null -> originName(node.origin)
                node.type != null -> typeLabel(node.type)
                else -> familyLabel(node.family)
            }
            FilterRow(
                label = label,
                count = node.count,
                depth = depth,
                state = node.state(excluded),
                onToggle = { onChange(node.toggle(excluded)) },
                expandable = node.children.isNotEmpty(),
                isExpanded = node.key in expanded,
                onExpand = { expanded = if (node.key in expanded) expanded - node.key else expanded + node.key },
            )
        }
    }
}

@Composable
private fun FilterRow(
    label: String,
    count: Int,
    depth: Int,
    state: ToggleableState,
    onToggle: () -> Unit,
    expandable: Boolean,
    isExpanded: Boolean,
    onExpand: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(start = (depth * 24).dp), verticalAlignment = Alignment.CenterVertically) {
        if (expandable) {
            val description = stringResource(if (isExpanded) Res.string.settings_events_collapse else Res.string.settings_events_expand, label)
            Text(
                if (isExpanded) "▾" else "▸",
                color = colors.onSurfaceVariant,
                modifier = Modifier.size(40.dp).clickable(onClick = onExpand).padding(top = 10.dp, start = 14.dp).semantics { contentDescription = description },
            )
        } else {
            Spacer(Modifier.width(40.dp))
        }
        Row(
            Modifier.weight(1f).triStateToggleable(state = state, role = Role.Checkbox, onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TriStateCheckbox(state = state, onClick = null, modifier = Modifier.padding(8.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.onSurface, modifier = Modifier.weight(1f))
            Text(pluralStringResource(Res.plurals.event_count, count, count), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant, modifier = Modifier.padding(end = 8.dp))
        }
    }
}
