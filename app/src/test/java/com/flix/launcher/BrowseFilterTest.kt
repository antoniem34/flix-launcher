package com.flix.launcher

import org.junit.Assert.*
import org.junit.Test

class BrowseFilterTest {
    private val movies = Addons.Row(Addons.CINEMETA, "Cinemeta", "movie", "top", "Movies")
    private val series = movies.copy(type = "series")
    private val anime = Addons.Row(Addons.KITSU, "Anime Kitsu", "anime", "kitsu-anime-trending", "Kitsu Trending")
    private fun title(id: String, type: String) = Catalog.Title(id, type, "Title", null, null, null, null, null, null, emptyList(), null)

    @Test fun animeCarouselIncludesBothAnimeMoviesAndSeries() {
        assertTrue(BrowseFilter.title("anime", title("kitsu:7442", "series")))
        assertTrue(BrowseFilter.title("anime", title("kitsu:11614", "movie")))
        assertFalse(BrowseFilter.title("anime", title("tt0816692", "movie")))
    }

    @Test fun animeTabContainsItsRowsWithoutGenericContinueWatchingOrMovieRankings() {
        assertTrue(BrowseFilter.row("anime", anime))
        assertFalse(BrowseFilter.row("anime", movies))
        assertFalse(BrowseFilter.row("anime", series))
        assertFalse(BrowseFilter.row("anime", movies.copy(manifest = "nuvio:continue"), continuing = true))
    }

    @Test fun ordinaryTabsDoNotMixKitsuFeaturedTitlesIntoTheirCarousel() {
        assertFalse(BrowseFilter.title("movie", title("kitsu:11614", "movie")))
        assertFalse(BrowseFilter.title("series", title("kitsu:7442", "series")))
        assertTrue(BrowseFilter.title("movie", title("tt0816692", "movie")))
        assertTrue(BrowseFilter.title("series", title("tt0903747", "series")))
    }

    @Test fun homeKeepsOrdinaryRowsAndOnlyUnfilteredAiringAnime() {
        for (row in listOf(movies, series)) assertTrue(BrowseFilter.row(null, row))
        assertFalse(BrowseFilter.row(null, anime))
        assertFalse(BrowseFilter.row(null, anime.copy(catalogId = "kitsu-anime-popular", extra = "genre=Isekai")))
        val airing = anime.copy(catalogId = "kitsu-anime-airing")
        assertTrue(BrowseFilter.row(null, airing))
        assertFalse(BrowseFilter.row(null, airing.copy(extra = "genre=Magic")))
        assertTrue(BrowseFilter.title(null, title("kitsu:7442", "series")))
        assertTrue(BrowseFilter.row("movie", movies))
        assertFalse(BrowseFilter.row("movie", series))
        assertTrue(BrowseFilter.row("series", series))
        assertTrue(BrowseFilter.row("series", movies, continuing = true))
    }

    @Test fun themedRowsKeepSeparateIdentityWithoutChangingOriginalKeys() {
        val popular = anime.copy(catalogId = "kitsu-anime-popular")
        assertEquals("${Addons.KITSU}|anime|kitsu-anime-popular", popular.key)
        assertNotEquals(popular.key, popular.copy(extra = "genre=Isekai").key)
        assertNotEquals(popular.copy(extra = "genre=Isekai").key, popular.copy(extra = "genre=Magic").key)
        assertTrue(BrowseFilter.row("anime", popular.copy(extra = "genre=Isekai")))
    }

    @Test fun compactHomeContainsOnlyContinueWatchingAndAiringAnime() {
        val localAiring = Addons.Row("http://192.168.1.10/manifest.json", "AnimES", "series", "animesv2-airing", "En emisión")
        val kitsuAiring = anime.copy(catalogId = "kitsu-anime-airing")
        val sources = listOf(movies, series, anime, localAiring, kitsuAiring)
        val visible = sources.filter { BrowseFilter.row(null, it, sources = sources, compactHome = true) }
        assertEquals(listOf(localAiring), visible)
        assertTrue(BrowseFilter.row(null, movies, continuing = true, compactHome = true))
        assertTrue(BrowseFilter.row(null, kitsuAiring, sources = listOf(kitsuAiring), compactHome = true))
        assertTrue(BrowseFilter.row(null, movies)) // Other profiles retain their layout.
    }

    @Test fun compactHomeKeepsCatalogueTabsSeparateFromContinueWatching() {
        val cartoonMovie = movies.copy(addonName = "CartoonES")
        val cartoonSeries = series.copy(addonName = "CartoonES")
        val donghua = series.copy(addonName = "DonghuaLife")
        val sources = listOf(cartoonMovie, cartoonSeries, anime, donghua)
        for ((tab, expected) in listOf("movie" to listOf(cartoonMovie), "series" to listOf(cartoonSeries), "anime" to listOf(anime, donghua))) {
            assertEquals(expected, sources.filter { BrowseFilter.row(tab, it, compactHome = true) })
            assertFalse(BrowseFilter.row(tab, movies, continuing = true, compactHome = true))
        }
    }

    @Test fun compactHomeHidesOnlyItsFeaturedCarousel() {
        assertFalse(BrowseFilter.carousel(null, compactHome = true))
        for (tab in listOf("movie", "series", "anime")) assertTrue(BrowseFilter.carousel(tab, compactHome = true))
        assertTrue(BrowseFilter.carousel(null, compactHome = false))
    }
}
