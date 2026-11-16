package io.github.fugeekies.timeflies.ui.events

import androidx.compose.ui.state.ToggleableState
import io.github.fugeekies.timeflies.domain.model.EventGroup
import io.github.fugeekies.timeflies.domain.model.EventGroupCount
import io.github.fugeekies.timeflies.domain.model.EventType
import io.github.fugeekies.timeflies.domain.model.Family
import kotlin.test.Test
import kotlin.test.assertEquals

class EventFilterTest {

    private val groups = listOf(
        EventGroupCount(EventGroup(EventType.RAP_ALBUM_HIT, "us"), 6),
        EventGroupCount(EventGroup(EventType.RAP_ALBUM_HIT, "fr"), 6),
        EventGroupCount(EventGroup(EventType.MUSIC_ICON_DEATH, "us", "rap"), 2),
        EventGroupCount(EventGroup(EventType.MUSIC_ICON_DEATH, "us", "rock"), 4),
        EventGroupCount(EventGroup(EventType.BLOCKBUSTER, "us"), 17),
    )
    private val tree = filterTree(groups)

    private fun node(key: String): FilterNode {
        fun List<FilterNode>.find(): FilterNode? = firstNotNullOfOrNull { if (it.key == key) it else it.children.find() }
        return tree.find() ?: error("no node $key")
    }

    @Test
    fun buildsFamilyTypeOriginAndSubcategory() {
        assertEquals(listOf(Family.CINEMA, Family.MUSIC), tree.map { it.family }, "in the order of the families")
        assertEquals(18, node("music").count)
        assertEquals(listOf("music/music-icon-death/us/rap", "music/music-icon-death/us/rock"), node("music/music-icon-death/us").children.map { it.key })
        assertEquals(emptyList(), node("music/rap-album-hit/us").children, "no subcategory: the origin is the file")
    }

    @Test
    fun aNodeIsTickedPartlyTickedOrUnticked() {
        val excluded = setOf("music/rap-album-hit/us", "music/music-icon-death/us/rap")
        assertEquals(ToggleableState.Off, node("music/rap-album-hit/us").state(excluded))
        assertEquals(ToggleableState.Indeterminate, node("music").state(excluded))
        assertEquals(ToggleableState.On, node("cinema").state(excluded))
    }

    @Test
    fun touchingANodeDoesItForEveryFileUnderIt() {
        val noUsRap = node("music/music-icon-death/us/rap").toggle(node("music/rap-album-hit/us").toggle(emptySet()))
        assertEquals(setOf("music/rap-album-hit/us", "music/music-icon-death/us/rap"), noUsRap)
        val noMusic = node("music").toggle(emptySet())
        assertEquals(4, noMusic.size)
        assertEquals(emptySet(), node("music").toggle(noUsRap), "a partly ticked node is ticked again entirely")
    }
}
