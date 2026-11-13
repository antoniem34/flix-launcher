package com.flix.launcher

/** Shared by tab switches and asynchronous catalogue/carousel updates. */
internal object BrowseFilter {
    fun row(type: String?, source: Addons.Row, continuing: Boolean = false, sources: List<Addons.Row> = emptyList(), compactHome: Boolean = false): Boolean {
        if (continuing) return type == null || !compactHome && type != "anime"
        return when (type) {
            null -> ((!compactHome && !AddonSource.animeRow(source)) || AddonSource.airing(source)) &&
                !(source.manifest == Addons.KITSU && sources.any { it.enabled && AddonSource.airing(it) && it.catalogId == "animesv2-airing" })
            "anime" -> AddonSource.animeRow(source)
            else -> source.type == type && !AddonSource.animeRow(source)
        }
    }

    fun carousel(type: String?, compactHome: Boolean): Boolean = type != null || !compactHome

    fun title(type: String?, title: Catalog.Title): Boolean = when (type) {
        null -> true
        "anime" -> title.isAnime
        else -> title.type == type && !title.isAnime
    }
}
