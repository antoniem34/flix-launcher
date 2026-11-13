package com.flix.launcher

/** Real Kitsu filters; fixed per row so the global genre setting cannot replace the theme. */
internal object KitsuThemes {
    val rows = listOf(
        "Isekai" to "Mundo Isekai",
        "Magic" to "Magia y hechicería",
        "Mecha" to "Mechas y robots",
        "School" to "Vida escolar",
        "Game" to "Juegos y mundos virtuales",
        "Samurai" to "Samuráis",
        "Supernatural" to "Sobrenatural",
    ).map { (genre, name) ->
        Addons.Row(Addons.KITSU, "Anime Kitsu", "anime", "kitsu-anime-popular", name,
            extra = "genre=$genre")
    }

    fun missing(existing: List<Addons.Row>): List<Addons.Row> =
        rows.filter { candidate -> existing.none { it.key == candidate.key } }
}
