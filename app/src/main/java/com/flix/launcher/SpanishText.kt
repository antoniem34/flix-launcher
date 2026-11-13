package com.flix.launcher

import java.util.Locale

/** Display labels only. Provider values and saved preferences keep their original IDs. */
internal object SpanishText {
    val locale: Locale = Locale.forLanguageTag("es-MX")

    fun searchKey(value: String): String = java.text.Normalizer
        .normalize(value, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").lowercase(locale)

    fun genre(value: String): String = when (value) {
        "Action" -> "Acción"
        "Adventure" -> "Aventura"
        "Animation" -> "Animación"
        "Biography" -> "Biografía"
        "Comedy" -> "Comedia"
        "Crime" -> "Crimen"
        "Documentary" -> "Documental"
        "Family" -> "Familiar"
        "Fantasy" -> "Fantasía"
        "History" -> "Historia"
        "Horror" -> "Terror"
        "Music" -> "Música"
        "Mystery" -> "Misterio"
        "News" -> "Noticias"
        "Reality", "Reality-TV" -> "Telerrealidad"
        "Sci-Fi", "Science Fiction" -> "Ciencia ficción"
        "Sport" -> "Deportes"
        "Thriller" -> "Suspenso"
        "War" -> "Guerra"
        "Western" -> "Del oeste"
        "Action & Adventure" -> "Acción y aventura"
        "Sci-Fi & Fantasy" -> "Ciencia ficción y fantasía"
        "War & Politics" -> "Guerra y política"
        "Kids" -> "Infantil"
        "Talk" -> "Entrevistas"
        "TV Movie" -> "Película para TV"
        "Short" -> "Cortometraje"
        else -> value
    }

    fun catalog(value: String): String = when (value) {
        "Kitsu Trending" -> "Anime en tendencia"
        "Kitsu Top Airing" -> "Anime en emisión"
        "Kitsu Most Popular" -> "Anime más popular"
        "Kitsu Highest Rated" -> "Anime mejor valorado"
        "Popular movies" -> "Películas populares"
        "Popular series" -> "Series populares"
        "Featured movies" -> "Películas destacadas"
        "Top 10 Movies Today" -> "Las 10 películas de hoy"
        else -> value
    }
}
