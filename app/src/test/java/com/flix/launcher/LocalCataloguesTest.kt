package com.flix.launcher

import org.junit.Assert.*
import org.junit.Test
import org.json.JSONObject

class LocalCataloguesTest {
    private val local = "http://192.168.1.10:3000"
    private val anime = Addons.Row("$local/manifest.json", "AnimES", "series", "animesv2-airing", "En emisión")

    @Test fun localAnimeRowsBelongToAnimeAndOnlyAiringBelongsToHome() {
        assertTrue(BrowseFilter.row("anime", anime))
        assertFalse(BrowseFilter.row("series", anime))
        assertTrue(BrowseFilter.row(null, anime))
        assertFalse(BrowseFilter.row(null, anime.copy(catalogId = "animesv2-kitsu-isekai")))
        assertTrue(BrowseFilter.row("anime", anime.copy(addonName = "DonghuaLife", catalogId = "donghualife")))
    }

    @Test fun localEpisodeIdentitySurvivesAnImdbMapping() {
        val title = Catalog.parse("""{"metas":[{"id":"animesv2:following:mal:1","imdb_id":"tt123","type":"series","name":"Anime"}]}""", local).single()
        assertEquals("animesv2:following:mal:1", title.id)
        assertTrue(BrowseFilter.title("anime", title))
        assertFalse(BrowseFilter.title("series", title))
    }

    @Test fun homePrefersLocalAiringWithoutDisablingSavedKitsuRows() {
        val kitsu = Addons.Row(Addons.KITSU, "Kitsu", "anime", "kitsu-anime-airing", "Anime en emisión")
        assertFalse(BrowseFilter.row(null, kitsu, sources = listOf(kitsu, anime)))
        assertTrue(BrowseFilter.row("anime", kitsu, sources = listOf(kitsu, anime)))
        assertTrue(BrowseFilter.row(null, kitsu, sources = listOf(kitsu, anime.copy(enabled = false))))
    }

    @Test fun imdbMetadataUsesConfiguredProvidersWithoutCinemeta() {
        assertEquals(listOf(local), NuvioAccount.metadataBasesForId("tt123", listOf(local)))
        assertFalse(NuvioAccount.metadataBasesForId("tt123", listOf(local, Addons.CINEMETA.removeSuffix("/manifest.json"))).any { it.contains("cinemeta") })
    }

    @Test fun httpImportsAndSearchKeepLocalAddressesAndEncodedConfiguration() {
        assertEquals("$local/manifest.json", Addons.normalise(local))
        assertEquals("$local/a%2Fb/manifest.json", Addons.normalise("$local/a%2Fb/"))
        assertEquals(listOf(anime.manifest), Catalog.searchManifestUrls(listOf(anime, anime.copy(manifest = Addons.CINEMETA))))
        assertThrows(IllegalArgumentException::class.java) { Addons.normalise("http://public.example/manifest.json") }
        assertThrows(IllegalArgumentException::class.java) { Addons.normalise(Addons.CINEMETA) }
        assertThrows(IllegalArgumentException::class.java) { Addons.normalise("http://192.168.1.10.evil.example") }
    }

    @Test fun manifestsKeepProviderNamesAndSkipSearchOnlyCatalogues() {
        val (_, rows) = Addons.parseManifest(anime.manifest, JSONObject("""{"name":"AnimES","catalogs":[
            {"id":"animesv2-airing","name":"En emisión","type":"series"},
            {"id":"search","type":"series","extra":[{"name":"search","isRequired":true}]}
        ]}"""))
        assertEquals(listOf(anime), rows)
    }

    @Test fun legacyDanimadosSearchDoesNotReturnItsUnrelatedDefaultList() {
        val titles = Catalog.parse("""{"metas":[
            {"id":"danimados_phineas","name":"Phineas y Ferb","type":"series"},
            {"id":"danimados_mansion","name":"La Mansión","type":"series"}
        ]}""", local)
        assertTrue(Catalog.localSearch(titles, "frieren").isEmpty())
        assertEquals(listOf("danimados_mansion"), Catalog.localSearch(titles, "mansion").map { it.id })
    }

    @Test fun localGenreQueriesUseTheProvidersSpanishOptions() {
        val row = anime.copy(supportsGenre = true, genreOptions = listOf("Acción", "Ciencia Ficción", "Comedia"))
        assertEquals("Acción", Catalog.genreValue(row, "Action"))
        assertEquals("Ciencia Ficción", Catalog.genreValue(row, "Sci-Fi"))
        assertNull(Catalog.genreValue(row, "Family"))
        assertNull(Catalog.genreValue(row.copy(supportsGenre = false), "Action"))
        assertEquals("Action", Catalog.genreValue(row.copy(genreOptions = emptyList()), "Action"))
    }

    @Test fun newProfilesPreserveExistingAnimeRowsWithoutCopyingThemToMovieProfiles() {
        val kitsu = Addons.Row(Addons.KITSU, "Kitsu", "anime", "popular", "Mi anime", enabled = false)
        val legacy = kitsu.copy(manifest = Addons.CINEMETA)
        assertEquals(listOf(kitsu), Addons.seedRows(listOf(kitsu, legacy), listOf(anime)))
        assertTrue(Addons.seedRows(listOf(kitsu, legacy), listOf(anime.copy(addonName = "CineSeriES", catalogId = "cineserieses-popular-series"))).isEmpty())
    }
}
