package com.flix.launcher

import org.junit.Assert.*
import org.junit.Test

class NuvioBackendTest {
    private val local = NuvioBackend.from("http://192.168.100.100:8080/")
    private fun discovery(url: String = local.url) = """{"version":1,"service":"nuvio","self_hosted":true,"backend_url":"$url","publishable_key":"public-test-key","capabilities":{"tv_login":true,"email_password_auth":true}}"""

    @Test fun cloudKeepsExistingSessionButLocalHasItsOwnSessionAndCache() {
        assertEquals("nuvio_account", NuvioBackend.CLOUD.preferenceName)
        assertEquals("continue_", NuvioBackend.CLOUD.cachePrefix)
        assertNotEquals(NuvioBackend.CLOUD.preferenceName, local.preferenceName)
        assertNotEquals(NuvioBackend.CLOUD.cachePrefix, local.cachePrefix)
        assertNotEquals(local.preferenceName, NuvioBackend.from("http://192.168.100.101:8080").preferenceName)
    }
    @Test fun localLoginAndCanonicalUrlStayOnTheSelectedServer() {
        assertEquals("http://192.168.100.100:8080", local.url)
        assertEquals("${local.url}/tv-login", local.loginUrl)
        assertEquals(NuvioBackend.CLOUD, NuvioBackend.from("https://API.NUVIO.TV:443/"))
    }
    @Test fun acceptsDiscoveryAndCapabilities() {
        val result = NuvioBackend.discover(local, discovery())
        assertEquals(local, result.backend)
        assertEquals("public-test-key", result.key)
        assertTrue(result.tvLogin)
        assertTrue(result.passwordLogin)
    }
    @Test fun rejectsDiscoveryThatRedirectsCredentialsToAnotherServer() {
        assertThrows(IllegalArgumentException::class.java) { NuvioBackend.discover(local, discovery("https://elsewhere.example")) }
        assertThrows(IllegalArgumentException::class.java) { NuvioBackend.discover(local, discovery().replace("\"nuvio\"", "\"other\"")) }
        assertThrows(IllegalArgumentException::class.java) { NuvioBackend.discover(local, discovery().replace("public-test-key", "")) }
    }
    @Test fun rejectsUnsafeOrAmbiguousAddresses() {
        for (url in listOf("ftp://example.org", "https://user:pass@example.org", "https://example.org?key=abc", "https://example.org/#x", "http://example.org")) {
            assertThrows(url, IllegalArgumentException::class.java) { NuvioBackend.from(url) }
        }
    }
    @Test fun aDomainContainingPrivateIpNumbersIsNotALocalAddress() {
        assertThrows(IllegalArgumentException::class.java) { NuvioBackend.from("http://192.168.1.1.example.org") }
    }
    @Test fun honorsPasswordOnlyServersInsteadOfInventingQrSupport() {
        val result = NuvioBackend.discover(local, discovery().replace("\"tv_login\":true", "\"tv_login\":false"))
        assertFalse(result.tvLogin)
        assertTrue(result.passwordLogin)
    }
}
