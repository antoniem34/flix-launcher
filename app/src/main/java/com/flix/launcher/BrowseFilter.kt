package com.flix.launcher

/** Shared by tab switches and asynchronous catalogue/carousel updates. */
internal object BrowseFilter {
    fun row(type: String?, source: Addons.Row, continuing: Boolean = false, sources: List<Addons.Row> = emptyList()): Boolean = when (type) {
        null -> (!AddonSource.animeRow(source) || AddonSource.airing(source)) &&
            !(source.manifest == Addons.KITSU && sources.any { it.enabled && it.catalogId == "animesv2-airing" })
        "anime" -> AddonSource.animeRow(source)
        else -> continuing || source.type == type && !AddonSource.animeRow(source)
    }

    fun title(type: String?, title: Catalog.Title): Boolean = when (type) {
        null -> true
        "anime" -> title.isAnime
        else -> title.type == type && !title.isAnime
    }
}
