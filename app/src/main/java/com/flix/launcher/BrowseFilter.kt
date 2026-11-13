package com.flix.launcher

/** Shared by tab switches and asynchronous catalogue/carousel updates. */
internal object BrowseFilter {
    fun row(type: String?, source: Addons.Row, continuing: Boolean = false): Boolean = when (type) {
        null -> true
        "anime" -> source.manifest == Addons.KITSU
        else -> continuing || source.type == type && source.manifest != Addons.KITSU
    }

    fun title(type: String?, title: Catalog.Title): Boolean = when (type) {
        null -> true
        "anime" -> title.isKitsu
        else -> title.type == type && !title.isKitsu
    }
}
