package com.flix.launcher

import java.net.URI

/** Shared URL and content classification for catalogues, search and metadata. */
internal object AddonSource {
    fun isCinemeta(url: String): Boolean = runCatching {
        URI(url).host?.lowercase() in setOf("v3-cinemeta.strem.io", "cinemeta.strem.io")
    }.getOrDefault(false)

    fun manifest(value: String): String {
        var url = value.trim().replaceFirst("stremio://", "https://")
        if (!url.contains("://")) url = "https://$url"
        NuvioBackend.from(url) // Validate the destination without decoding configured addon paths.
        val canonical = url.trimEnd('/')
        require(!isCinemeta(canonical)) { "Cinemeta está desactivado; agrega uno de tus complementos" }
        return if (canonical.endsWith("/manifest.json")) canonical else "$canonical/manifest.json"
    }

    fun supported(url: String): Boolean = runCatching { manifest(url) }.isSuccess

    fun local(url: String): Boolean = runCatching {
        val uri = URI(url)
        NuvioBackend.from("http://${uri.rawAuthority}")
        true
    }.getOrDefault(false)

    fun animeId(id: String): Boolean = id.startsWith("kitsu:") || id.startsWith("animesv2:") ||
        id.startsWith("donghualife_") || id.startsWith("mundodonghua_")

    fun animeRow(row: Addons.Row): Boolean = row.manifest == Addons.KITSU || row.type == "anime" ||
        row.addonName.equals("AnimES", true) || row.addonName.contains("donghua", true) ||
        row.catalogId.startsWith("animesv2-")

    fun airing(row: Addons.Row): Boolean = row.extra.isBlank() &&
        row.catalogId in setOf("kitsu-anime-airing", "animesv2-airing")
}
