package com.flix.launcher

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * The catalogue rows on the home screen, in order. Each row is one catalogue of
 * a Stremio-compatible addon (the same addons Nuvio uses), identified by the
 * addon's manifest URL. Local Nuvio profiles keep independent lists.
 */
object Addons {
    data class Row(
        val manifest: String,
        val addonName: String,
        val type: String,
        val catalogId: String,
        val title: String,
        val enabled: Boolean = true,
        val supportsGenre: Boolean = false,
        /** Extra query the catalogue needs, e.g. "skip=0"; blank when none. */
        val extra: String = "",
        val genreOptions: List<String> = emptyList(),
    ) {
        val key get() = "$manifest|$type|$catalogId" + if (extra.isBlank()) "" else "|$extra"
        val base get() = manifest.removeSuffix("/manifest.json").removeSuffix("/")
        val displayTitle get() = if (manifest == CINEMETA || manifest == KITSU) SpanishText.catalog(title) else title
    }

    const val CINEMETA = "https://v3-cinemeta.strem.io/manifest.json"
    const val KITSU = "https://anime-kitsu.strem.fun/manifest.json"

    private val DEFAULT_ROWS = emptyList<Row>()

    /**
     * Rows are edited from the TV screen and from the phone page at the same time;
     * every read-modify-save holds this lock so one edit never overwrites another.
     */
    private val lock = Any()

    private fun prefs(context: Context) = context.getSharedPreferences("flix", Context.MODE_PRIVATE)

    private fun rowKey(context: Context) = NuvioAccount.catalogueScope(context)?.let { "${KEY}_local_${Catalog.hashName(it)}" } ?: KEY

    fun rows(context: Context): List<Row> = readRows(context, rowKey(context))

    /** Stored with this profile's catalogues, without changing global appearance settings. */
    fun compactHome(context: Context): Boolean = prefs(context).getBoolean("compact_home_${rowKey(context)}", false)

    fun setCompactHome(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean("compact_home_${rowKey(context)}", enabled).apply()

    private fun readRows(context: Context, key: String): List<Row> {
        val raw = prefs(context).getString(key, null) ?: return DEFAULT_ROWS
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Row(
                    manifest = o.getString("manifest"),
                    addonName = o.optString("addonName"),
                    type = o.getString("type"),
                    catalogId = o.getString("catalogId"),
                    title = o.getString("title"),
                    enabled = o.optBoolean("enabled", true),
                    supportsGenre = o.optBoolean("supportsGenre"),
                    extra = o.optString("extra"),
                    genreOptions = o.optJSONArray("genreOptions")?.let { g -> (0 until g.length()).map { g.getString(it) } }.orEmpty(),
                )
            }.filterNot { AddonSource.isCinemeta(it.manifest) }
        }.getOrDefault(DEFAULT_ROWS)
    }

    fun save(context: Context, rows: List<Row>) = saveRows(context, rowKey(context), rows)

    private fun saveRows(context: Context, key: String, rows: List<Row>) = synchronized(lock) {
        val array = JSONArray()
        rows.filterNot { AddonSource.isCinemeta(it.manifest) }.forEach { r ->
            array.put(JSONObject().apply {
                put("manifest", r.manifest)
                put("addonName", r.addonName)
                put("type", r.type)
                put("catalogId", r.catalogId)
                put("title", r.title)
                put("enabled", r.enabled)
                put("supportsGenre", r.supportsGenre)
                put("extra", r.extra)
                put("genreOptions", JSONArray(r.genreOptions))
            })
        }
        prefs(context).edit().putString(key, array.toString()).apply()
    }

    /** Removes legacy provider rows without changing any appearance or app preference. */
    fun removeCinemeta(context: Context) = synchronized(lock) {
        prefs(context).all.keys.filter { it == KEY || it.startsWith("${KEY}_local_") }.forEach { key ->
            val raw = prefs(context).getString(key, null).orEmpty()
            if (raw.contains("cinemeta.strem.io")) saveRows(context, key, readRows(context, key))
        }
    }

    fun move(context: Context, index: Int, delta: Int) = synchronized(lock) {
        val list = rows(context).toMutableList()
        val to = index + delta
        if (index !in list.indices || to !in list.indices) return
        list.add(to, list.removeAt(index))
        save(context, list)
    }

    fun toggle(context: Context, index: Int) = synchronized(lock) {
        val list = rows(context).toMutableList()
        if (index !in list.indices) return
        list[index] = list[index].copy(enabled = !list[index].enabled)
        save(context, list)
    }

    /** Turns a row on or off; a row switched on joins the end of the rows already on the home screen. */
    fun setEnabled(context: Context, key: String, on: Boolean) = synchronized(lock) {
        val list = rows(context).toMutableList()
        val index = list.indexOfFirst { it.key == key }
        if (index < 0) return
        val row = list.removeAt(index).copy(enabled = on)
        val insertAt = if (on) list.indexOfLast { it.enabled } + 1 else list.size.coerceAtMost(index).let { i ->
            // Keep a switched-off row near where it was, but after every row still on.
            maxOf(i, list.indexOfLast { it.enabled } + 1)
        }
        list.add(insertAt, row)
        save(context, list)
    }

    /** Moves a row among the rows that are on, by key. */
    fun moveEnabled(context: Context, key: String, delta: Int) = synchronized(lock) {
        val list = rows(context)
        val index = list.indexOfFirst { it.key == key }
        val target = index + delta
        if (index < 0 || target !in list.indices || !list[target].enabled) return
        move(context, index, delta)
    }

    fun removeAddon(context: Context, manifest: String) = synchronized(lock) {
        save(context, rows(context).filter { it.manifest != manifest })
    }

    fun remove(context: Context, index: Int) = synchronized(lock) {
        val list = rows(context).toMutableList()
        if (index !in list.indices) return
        list.removeAt(index)
        save(context, list)
    }

    fun reset(context: Context) = synchronized(lock) { prefs(context).edit().putString(rowKey(context), "[]").apply() }

    /** Appends missing thematic rows without re-enabling or reordering existing rows. */
    fun addAnimeThemes(context: Context): Int = synchronized(lock) {
        val existing = rows(context)
        val fresh = KitsuThemes.missing(existing)
        if (fresh.isNotEmpty()) save(context, existing + fresh)
        fresh.size
    }

    /**
     * Reads an addon's manifest and appends one row per browsable catalogue
     * (catalogues that need a search term are skipped). Runs on a background thread.
     * Returns the number of rows added, or throws with a readable message.
     */
    fun add(context: Context, url: String): Int {
        val key = rowKey(context)
        val (name, found) = catalogues(normalise(url))
        // The download can take seconds, so the rows are read again under the lock just before saving.
        val added = synchronized(lock) {
            val existing = readRows(context, key)
            val fresh = found.filter { f -> existing.none { it.key == f.key } }
            if (fresh.isEmpty()) throw IllegalArgumentException(
                if (found.isEmpty()) "$name no tiene catálogos compatibles con Flix Latino" else "Las filas de $name ya están agregadas"
            )
            // Import all catalogues; the user chooses which rows to display.
            saveRows(context, key, existing + fresh.map { it.copy(enabled = false) })
            fresh.size
        }
        return added
    }

    /**
     * Adds every catalogue of the addons in the user's Nuvio profile, switched off, so the
     * Addons & rows screen lists them all and the user picks which appear at home. Rows
     * already here keep their place and state. Addons that can't be read are skipped.
     * Runs on a background thread; returns how many rows were added.
     */
    fun syncFromNuvio(context: Context): Int {
        val selection = NuvioAccount.selectionKey(context)
        val key = rowKey(context)
        val local = !NuvioAccount.backend(context).isCloud
        val found = NuvioAccount.addonUrls(context).filter { AddonSource.supported(it) && (!local || AddonSource.local(it)) }.flatMap { url ->
            runCatching { catalogues(normalise(url)).second }.getOrDefault(emptyList())
        }
        if (found.isEmpty()) return 0
        return synchronized(lock) {
            if (selection != NuvioAccount.selectionKey(context)) return@synchronized 0
            val initialized = prefs(context).contains(key)
            val existing = if (initialized) readRows(context, key) else seedRows(readRows(context, KEY), found)
            val refreshed = existing.map { row -> found.firstOrNull { it.key == row.key }?.let {
                row.copy(supportsGenre = it.supportsGenre, genreOptions = it.genreOptions)
            } ?: row }
            val fresh = found.filter { f -> existing.none { it.key == f.key } }.distinctBy { it.key }
            if (fresh.isNotEmpty() || !initialized || refreshed != existing) saveRows(context, key,
                refreshed + fresh.map { it.copy(enabled = !initialized && local && initiallyEnabled(it)) })
            fresh.size + refreshed.zip(existing).count { (a, b) -> a != b }
        }
    }

    internal fun seedRows(previous: List<Row>, incoming: List<Row>): List<Row> = previous
        .filterNot { AddonSource.isCinemeta(it.manifest) }
        .filter { !AddonSource.animeRow(it) || incoming.any(AddonSource::animeRow) }

    internal fun initiallyEnabled(row: Row): Boolean = row.catalogId in setOf(
        "animesv2-airing", "animesv2-following", "donghualife", "mundodonghua", "lacart_catalogo", "danimados"
    ) || row.catalogId.endsWith("popular-series") || row.catalogId.endsWith("popular-movies") ||
        row.catalogId.endsWith("airing-series") || row.catalogId.endsWith("following-series") || row.catalogId.endsWith("following-movies")

    /** An addon's name and one row per browsable catalogue (ones that need a search term are skipped). */
    private fun catalogues(manifestUrl: String): Pair<String, List<Row>> {
        require(AddonSource.supported(manifestUrl)) { "Usa HTTPS o HTTP de tu red local; Cinemeta está desactivado" }
        val conn = URL(manifestUrl).openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent", "FlixLatino/1.0 (Android TV)")
        conn.connectTimeout = 8000
        conn.readTimeout = 10000
        conn.instanceFollowRedirects = true
        val manifest = try {
            JSONObject(SafeIo.readText(conn))
        } catch (e: Exception) {
            throw IllegalArgumentException("No se pudo leer el complemento (${e.javaClass.simpleName})")
        } finally {
            conn.disconnect()
        }
        return parseManifest(manifestUrl, manifest)
    }

    internal fun parseManifest(manifestUrl: String, manifest: JSONObject): Pair<String, List<Row>> {
        val name = manifest.optString("name", "Complemento")
        val catalogs = manifest.optJSONArray("catalogs") ?: JSONArray()
        val found = mutableListOf<Row>()
        for (i in 0 until catalogs.length()) {
            val c = catalogs.getJSONObject(i)
            val extras = c.optJSONArray("extra") ?: JSONArray()
            var needsInput = false
            var genre = false
            var genreOptions = emptyList<String>()
            for (j in 0 until extras.length()) {
                val e = extras.getJSONObject(j)
                when (e.optString("name")) {
                    "genre" -> {
                        genre = true
                        genreOptions = e.optJSONArray("options")?.let { g -> (0 until g.length()).map { g.getString(it) } }.orEmpty()
                    }
                    "skip" -> {}
                    else -> if (e.optBoolean("isRequired")) needsInput = true
                }
            }
            val required = c.optJSONArray("extraRequired")
            if (required != null && (0 until required.length()).any { required.getString(it) != "genre" }) needsInput = true
            if (needsInput) continue
            val row = Row(
                manifest = manifestUrl,
                addonName = name,
                type = c.optString("type", "movie"),
                catalogId = c.getString("id"),
                title = c.optString("name").ifBlank { "$name ${c.optString("type")}" },
                supportsGenre = genre,
                genreOptions = genreOptions,
            )
            if (found.none { it.key == row.key }) found += row
        }
        return name to found
    }

    /** Accepts stremio:// links and URLs pasted without /manifest.json. */
    fun normalise(url: String): String = AddonSource.manifest(url)

    private const val KEY = "catalogue_rows"
}
