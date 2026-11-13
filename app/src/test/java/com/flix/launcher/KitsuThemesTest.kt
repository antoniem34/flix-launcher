package com.flix.launcher

import org.junit.Assert.*
import org.junit.Test

class KitsuThemesTest {
    @Test fun addingThemesKeepsExistingRowsAndIsRepeatableWithoutDuplicates() {
        val original = Addons.Row(Addons.KITSU, "Anime Kitsu", "anime", "kitsu-anime-popular", "Kitsu Most Popular")
        val fresh = KitsuThemes.missing(listOf(original))
        assertEquals(7, fresh.size)
        assertEquals(8, (listOf(original) + fresh).map { it.key }.distinct().size)
        assertTrue(KitsuThemes.missing(listOf(original) + fresh).isEmpty())
    }

    @Test fun disabledOrRenamedThemesAreNotAddedAgain() {
        val existing = KitsuThemes.rows.map { it.copy(enabled = false, title = "Mi nombre") }.reversed()
        assertTrue(KitsuThemes.missing(existing).isEmpty())
        assertTrue(existing.all { !it.enabled })
    }

    @Test fun themesAreAvailableOnlyInAnimeAndKeepTheirFixedFilters() {
        for (row in KitsuThemes.rows) {
            assertTrue(BrowseFilter.row("anime", row))
            assertFalse(BrowseFilter.row(null, row))
            assertFalse(BrowseFilter.row("movie", row))
            assertFalse(BrowseFilter.row("series", row))
            assertFalse(row.supportsGenre)
            assertTrue(row.extra.startsWith("genre="))
        }
        assertEquals("genre=Isekai", KitsuThemes.rows.first { it.title == "Mundo Isekai" }.extra)
    }
}
