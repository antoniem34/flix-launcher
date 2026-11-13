package com.flix.launcher

import org.junit.Assert.*
import org.junit.Test

class KitsuCatalogTest {
    private val base = "https://anime-kitsu.strem.fun"

    @Test fun animeSeasonsWithSharedImdbRemainDistinctAndOpenTheirOwnAddonDetails() {
        val titles = Catalog.parse("""{"metas":[
            {"id":"kitsu:11469","imdb_id":"tt5626028","type":"series","name":"My Hero Academia"},
            {"id":"kitsu:12268","imdb_id":"tt5626028","type":"series","name":"My Hero Academia 2"}
        ]}""", base)
        assertEquals(listOf("kitsu:11469", "kitsu:12268"), titles.map { it.id })
        assertEquals(listOf("tt5626028", "tt5626028"), titles.map { it.synopsisId })
        assertTrue(titles[1].nuvioUri.startsWith("nuvio://detail/series/kitsu%3A12268?"))
        assertTrue(titles[1].nuvioUri.contains("addonBaseUrl=https%3A%2F%2Fanime-kitsu.strem.fun"))
    }

    @Test fun animeMoviesKeepTheirOwnTypeAndKitsuIdentity() {
        val title = Catalog.parse("""{"metas":[
            {"id":"kitsu:11614","imdb_id":"tt5311514","type":"movie","name":"Your Name"}
        ]}""", base).single()
        assertEquals("kitsu:11614", title.id)
        assertEquals("movie", title.type)
        assertEquals("tt5311514", title.synopsisId)
        assertEquals("Kitsu", title.ratingLabel)
        assertTrue(title.nuvioUri.startsWith("nuvio://detail/movie/kitsu%3A11614?"))
    }

    @Test fun ordinaryCatalogsKeepTheirExistingImdbBehavior() {
        val title = Catalog.parse("""{"metas":[
            {"id":"provider:42","imdb_id":"tt0816692","type":"movie","name":"Interstellar"}
        ]}""", "https://example.com").single()
        assertEquals("tt0816692", title.id)
        assertEquals("nuvio://detail/movie/tt0816692", title.nuvioUri)
        assertEquals("tt0816692", title.synopsisId)
        assertEquals("IMDb", title.ratingLabel)
    }

    @Test fun unmappedAnimeAndMalformedMappingsNeverLeakAnIdToTheTranslationProvider() {
        for (mapping in listOf("", "null", "private:token", "tt123/../../secret")) {
            val title = Catalog.parse("""{"metas":[
                {"id":"kitsu:99999","imdb_id":"$mapping","type":"series","name":"New anime"}
            ]}""", base).single()
            assertEquals("kitsu:99999", title.id)
            assertNull(title.synopsisId)
        }
    }

    @Test fun kitsuLabelsAreTranslatedWithoutRenamingCustomCatalogs() {
        val row = Addons.Row(Addons.KITSU, "Anime Kitsu", "anime", "kitsu-anime-trending", "Kitsu Trending")
        assertEquals("Anime en tendencia", row.displayTitle)
        assertEquals("Mi anime", row.copy(title = "Mi anime").displayTitle)
        assertEquals("Kitsu Trending", row.copy(manifest = "https://example.com/manifest.json").displayTitle)
    }
}
