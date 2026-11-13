package com.flix.launcher

import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Description-only enrichment: catalogue IDs, ordering and playback links stay untouched. */
internal class SynopsisSource(
    private val directory: File,
    private val clock: () -> Long = System::currentTimeMillis,
    private val fetch: (String) -> String = ::download,
) {
    private data class Entry(val text: String?, val expires: Long)
    private val memory = mutableMapOf<String, Entry>()

    // Used by one background executor. Failed requests are retried later, never on every focus.
    fun load(type: String, id: String): String? {
        val url = endpoint(type, id) ?: return null
        val key = "${type}_${id.replace(':', '_')}"
        val now = clock()
        memory[key]?.takeIf { now < it.expires }?.let { return it.text }
        val file = File(directory, "$key.json")
        val disk = runCatching {
            val json = JSONObject(file.readText())
            Entry(json.getString("description"), json.getLong("fetchedAt") + FRESH_MS)
        }.getOrNull()
        if (disk != null && now < disk.expires) {
            memory[key] = disk
            return disk.text
        }
        val translated = runCatching { description(fetch(url), type) }.getOrNull()
        val text = translated ?: memory[key]?.text ?: disk?.text
        memory[key] = Entry(text, now + if (translated != null) FRESH_MS else RETRY_MS)
        if (translated != null) runCatching {
            directory.mkdirs()
            SafeIo.write(file, JSONObject().put("description", translated).put("fetchedAt", now).toString())
        }
        return text
    }

    companion object {
        private const val FRESH_MS = 7 * 24 * 60 * 60 * 1000L
        private const val RETRY_MS = 15 * 60 * 1000L
        private val supportedId = Regex("(?:tt[0-9]+|tmdb:[0-9]+)")

        fun endpoint(type: String, id: String): String? {
            if (type !in listOf("movie", "series") || !supportedId.matches(id)) return null
            // Only public title IDs go to the provider; no addon URLs or account credentials.
            return "https://tmdb.elfhosted.com/es-MX/meta/$type/$id.json"
        }

        fun description(body: String, type: String): String? {
            val meta = JSONObject(body).optJSONObject("meta") ?: return null
            if (meta.has("type") && meta.optString("type") != type) return null
            return (meta.opt("description") as? String)?.trim()?.takeIf { it.isNotEmpty() }
        }

        private fun download(url: String): String {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.setRequestProperty("Accept", "application/json")
            return try {
                check(conn.responseCode == 200) { "Metadata unavailable" }
                SafeIo.readText(conn)
            } finally { conn.disconnect() }
        }
    }
}
