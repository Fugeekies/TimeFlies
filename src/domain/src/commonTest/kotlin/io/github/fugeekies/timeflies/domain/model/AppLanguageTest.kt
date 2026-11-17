package io.github.fugeekies.timeflies.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppLanguageTest {

    @Test
    fun readsATagWithOrWithoutARegion() {
        assertEquals(AppLanguage.GERMAN, AppLanguage.of("de"))
        assertEquals(AppLanguage.GERMAN, AppLanguage.of("de-AT"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.of("ES_mx"))
        assertNull(AppLanguage.of("pt-BR"), "the interface is not in Portuguese")
    }

    @Test
    fun prefersTheChoiceThenTheSystemThenFrench() {
        assertEquals(AppLanguage.ITALIAN, AppLanguage.resolve(AppLanguage.ITALIAN, "en-GB"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.resolve(null, "en-GB"))
        assertEquals(AppLanguage.FRENCH, AppLanguage.resolve(null, "ja-JP"), "French when the system language is not available")
    }
}
