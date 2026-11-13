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

    @Test fun homeKeepsTheCombinedCatalogAndOrdinaryTabsKeepTheirRows() {
        for (row in listOf(movies, series, anime)) assertTrue(BrowseFilter.row(null, row))
        assertTrue(BrowseFilter.title(null, title("kitsu:7442", "series")))
        assertTrue(BrowseFilter.row("movie", movies))
        assertFalse(BrowseFilter.row("movie", series))
        assertTrue(BrowseFilter.row("series", series))
        assertTrue(BrowseFilter.row("series", movies, continuing = true))
    }
}
