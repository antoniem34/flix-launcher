package com.flix.launcher

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SynopsisSourceTest {
    @get:Rule val temp = TemporaryFolder()
    private val response = """{"meta":{"id":"tmdb:157336","type":"movie","description":"Una aventura espacial."}}"""

    @Test fun onlyPublicSupportedIdsAreSentToProvider() {
        assertEquals("https://tmdb.elfhosted.com/es-MX/meta/movie/tt0816692.json",
            SynopsisSource.endpoint("movie", "tt0816692"))
        assertNotNull(SynopsisSource.endpoint("series", "tmdb:1396"))
        assertNull(SynopsisSource.endpoint("movie", "custom:private-token"))
        assertNull(SynopsisSource.endpoint("movie", "tt123/../../secret"))
        assertNull(SynopsisSource.endpoint("channel", "tt123"))
    }

    @Test fun missingAndWrongTypeMetadataKeepOriginalDescription() {
        assertEquals("Una aventura espacial.", SynopsisSource.description(response, "movie"))
        assertNull(SynopsisSource.description(response, "series"))
        assertNull(SynopsisSource.description("""{"meta":null}""", "movie"))
        assertNull(SynopsisSource.description("""{"meta":{"description":null}}""", "movie"))
        assertNull(SynopsisSource.description("""{"meta":{"description":"  "}}""", "movie"))
    }

    @Test fun cachedTranslationSurvivesRestartWithoutNetworkCall() {
        val dir = temp.newFolder()
        var requests = 0
        val source = SynopsisSource(dir, { 1000L }) { requests++; response }
        assertEquals("Una aventura espacial.", source.load("movie", "tt0816692"))
        assertEquals("Una aventura espacial.", source.load("movie", "tt0816692"))
        val restarted = SynopsisSource(dir, { 2000L }) { requests++; error("offline") }
        assertEquals("Una aventura espacial.", restarted.load("movie", "tt0816692"))
        assertEquals(1, requests)
    }

    @Test fun staleTranslationSurvivesOutageAndLaterRefreshes() {
        val dir = temp.newFolder()
        var now = 1000L
        var offline = false
        var requests = 0
        val source = SynopsisSource(dir, { now }) {
            requests++
            if (offline) error("offline")
            response.replace("Una aventura espacial.", "Sinopsis $requests")
        }
        assertEquals("Sinopsis 1", source.load("movie", "tt0816692"))
        now += 8 * 24 * 3600_000L
        offline = true
        assertEquals("Sinopsis 1", source.load("movie", "tt0816692"))
        assertEquals("Sinopsis 1", source.load("movie", "tt0816692"))
        assertEquals(2, requests)
        now += 16 * 60_000L
        offline = false
        assertEquals("Sinopsis 3", source.load("movie", "tt0816692"))
    }

    @Test fun missingTranslationAndMalformedResponseAreRetriedAfterCooldown() {
        var now = 1000L
        var requests = 0
        val source = SynopsisSource(temp.newFolder(), { now }) {
            requests++
            when (requests) { 1 -> "not json"; 2 -> """{"meta":null}"""; else -> response }
        }
        assertNull(source.load("movie", "tt0816692"))
        assertNull(source.load("movie", "tt0816692"))
        assertEquals(1, requests)
        now += 16 * 60_000L
        assertNull(source.load("movie", "tt0816692"))
        now += 16 * 60_000L
        assertEquals("Una aventura espacial.", source.load("movie", "tt0816692"))
    }

    @Test fun unsupportedIdsNeverReachNetwork() {
        val source = SynopsisSource(temp.newFolder()) { error("must not fetch") }
        assertNull(source.load("movie", "custom:123"))
    }
}
