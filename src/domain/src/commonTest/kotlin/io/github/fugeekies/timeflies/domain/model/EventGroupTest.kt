package io.github.fugeekies.timeflies.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EventGroupTest {

    @Test
    fun readsTheGroupFromItsPath() {
        val group = assertIs<EventGroup.Result.Valid>(EventGroup.parse("music/music-icon-death/us/rap")).group
        assertEquals(EventGroup(EventType.MUSIC_ICON_DEATH, "us", "rap"), group)
        assertEquals("music/music-icon-death/us/rap", group.path)
        assertEquals(Family.MUSIC, group.family)
        assertEquals("sport/olympics-in-france/international", EventGroup(EventType.OLYMPICS_IN_FRANCE, EventGroup.INTERNATIONAL).path)
    }

    @Test
    fun refusesWhatIsNotAGroup() {
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("music/rap-album-hit"), "origin missing")
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("music/unknown/us"))
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("cinema/rap-album-hit/us"), "type of another family")
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("music/rap-album-hit/USA"), "origin: two lower-case letters")
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("music/rap-album-hit/us/Rap US"))
        assertIs<EventGroup.Result.Invalid>(EventGroup.parse("music/rap-album-hit/us/rap/extra"))
    }
}
