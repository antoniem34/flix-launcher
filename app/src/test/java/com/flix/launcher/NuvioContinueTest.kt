package com.flix.launcher

import org.junit.Assert.*
import org.junit.Test
import org.json.JSONObject

class NuvioContinueTest {
    private val kitsu = Addons.Row(Addons.KITSU, "Kitsu", "anime", "popular", "Anime")

    @Test fun localProfileAddonsSupplyMetadataWithoutAddingHomeRows() {
        val rows = listOf(kitsu)
        val urls = listOf("http://192.168.1.10:7000/my-config/manifest.json", "https://addon.example/manifest.json")
        assertEquals(listOf("http://192.168.1.10:7000/my-config", "https://addon.example", kitsu.base),
            NuvioAccount.metadataBases(urls, rows))
        assertEquals(listOf(kitsu), rows)
    }

    @Test fun duplicateAndUnsupportedMetadataSourcesAreExcluded() {
        assertEquals(listOf(kitsu.base), NuvioAccount.metadataBases(
            listOf(Addons.KITSU, Addons.KITSU, "file:///private/manifest.json", "nuvio:continue"), listOf(kitsu)))
    }

    @Test fun kitsuKeepsItsMappingForSpanishSynopsisEvenWhenAProfileAddonAlsoAnswers() {
        val local = "http://192.168.1.10:7000"
        assertEquals(listOf(kitsu.base, local), NuvioAccount.metadataBasesForId("kitsu:46474", listOf(local, kitsu.base)))
        assertEquals(listOf(local, kitsu.base), NuvioAccount.metadataBasesForId("animesv2:following:mal:35834", listOf(local, kitsu.base)))
    }

    @Test fun continueWatchingKeepsTheActualAnimeEpisodeIdentity() {
        fun title(id: String, video: String?) = Catalog.Title(id, "series", "Anime", null, null, null, null, null,
            null, emptyList(), null, season = 1, episode = 1, videoId = video)
        assertEquals("animesv2:animeav1:angolmois-genkou-kassenki:1",
            title("animesv2:following:mal:35834", "animesv2:animeav1:angolmois-genkou-kassenki:1").streamVideoId)
        assertEquals("kitsu:46474:1", title("kitsu:46474", "kitsu:46474:1").streamVideoId)
        assertNull(title("kitsu:46474", null).streamVideoId)
        assertNull(title("animesv2:following:mal:35834", null).streamVideoId)
        assertEquals("tt0903747:1:1", title("tt0903747", null).streamVideoId)
    }

    @Test fun progressKeepsTheSavedVideoAndUpNextUsesTheProvidersExactId() {
        val progress = JSONObject("""{"video_id":"animesv2:pluto:es:show:opaque-first"}""")
        val meta = JSONObject("""{"videos":[{"id":"animesv2:pluto:es:show:opaque-second","season":1,"episode":2}]}""")
        assertEquals("animesv2:pluto:es:show:opaque-first", NuvioAccount.progressVideoId(progress, meta, 1, 1, false))
        assertEquals("animesv2:pluto:es:show:opaque-second", NuvioAccount.progressVideoId(progress, meta, 1, 2, true))
        assertNull(NuvioAccount.progressVideoId(progress, meta, 1, 3, true))
    }
}
