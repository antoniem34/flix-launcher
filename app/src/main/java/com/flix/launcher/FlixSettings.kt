package com.flix.launcher

import android.content.Context

/**
 * Every user-facing option in Flix Launcher. Each one is a list of choices stored by id;
 * on/off switches are just two-choice options. The settings screen lists
 * each one with its icon and search words.
 */
object FlixSettings {
    class Option(
        val key: String,
        val title: String,
        val choices: List<Pair<String, String>>, // id to label
        val default: String,
        val summary: String? = null,
    ) {
        fun label(id: String) = choices.firstOrNull { it.first == id }?.second ?: id
    }

    private fun toggle(key: String, title: String, default: Boolean = true, summary: String? = null) =
        Option(key, title, listOf("on" to "Activado", "off" to "Desactivado"), if (default) "on" else "off", summary)

    // Look
    val THEME = Option("theme", "Tema",
        listOf("cinema" to "Cine", "glass" to "Cristal", "midnight" to "Medianoche", "minimal" to "Minimalista"), "cinema",
        summary = "Colores y formas del inicio")

    // What the Home button lands on
    val HOME_FOCUS = Option("home_focus", "Inicio muestra",
        listOf("apps" to "Tus aplicaciones", "watching" to "Lo que estabas viendo"), "apps",
        summary = "Abre tus apps o continúa lo que estabas viendo")

    // Icons
    val ICON_SHAPE = Option("icon_shape", "Forma del icono",
        listOf("circle" to "Círculo", "squircle" to "Superelipse", "rounded" to "Cuadrado redondeado", "banner" to "Banner ancho"), "circle",
        summary = "Forma de los iconos de las aplicaciones")
    val ICON_SIZE = Option("icon_size", "Tamaño del icono", listOf("small" to "Pequeño", "medium" to "Mediano", "large" to "Grande"), "medium")
    val LABELS = Option("labels", "Nombres de apps", listOf("always" to "Siempre", "focus" to "Al seleccionar", "never" to "Ocultos"), "always")
    val GLOW = toggle("glow", "Brillo de color", summary = "Las apps seleccionadas brillan con su propio color")
    val MOTION = Option("motion", "Animación del foco", listOf("spring" to "Con rebote", "smooth" to "Suave", "none" to "Desactivado"), "spring")

    // Clock & widgets
    val CLOCK_STYLE = Option("clock_style", "Estilo del reloj", listOf("duo" to "Dúo", "thin" to "Fino", "bold" to "Grueso"), "duo")
    val CLOCK_FORMAT = Option("clock_format", "Formato de hora", listOf("system" to "Sistema", "12" to "12 horas", "24" to "24 horas"), "system")
    val GREETING = toggle("greeting", "Saludo")
    val DATE = toggle("date", "Fecha")
    val SUN_ARC = toggle("sun_arc", "Arco de amanecer y atardecer")
    val WEATHER = toggle("weather", "Clima", summary = "Indicador en la esquina superior derecha")
    val FORECAST = toggle("forecast", "Pronóstico de 3 días")
    val UNITS = Option("units", "Temperatura", listOf("c" to "Celsius", "f" to "Fahrenheit"), "c")

    // Wallpaper
    val WALLPAPER = Option("wallpaper", "Fondo", listOf(
        "nature" to "Fotos de naturaleza", "cities" to "Fotos de ciudades",
        "wildlife" to "Fotos de fauna", "beaches" to "Fotos de playas", "winter" to "Fotos de invierno",
        "deserts" to "Fotos de desiertos", "flowers" to "Fotos de flores", "night" to "Fotos del cielo nocturno", "castles" to "Castillos y templos",
        "films" to "Éxitos de cine", "films_scifi" to "Ciencia ficción", "films_action" to "Películas de acción", "films_animation" to "Películas animadas", "films_fantasy" to "Películas de fantasía", "films_horror" to "Películas de terror", "scifi" to "Arte de ciencia ficción", "gaming" to "Arte de videojuegos",
        "video" to "Todos los videos aéreos", "v_nature" to "Videos de paisajes", "v_sea" to "Videos de océanos",
        "v_city" to "Videos de ciudades", "v_space" to "La Tierra desde el espacio", "v_art" to "Videos de arte de color",
        "v_underwater" to "Videos submarinos", "v_beaches" to "Videos de playas", "v_wildlife" to "Videos de fauna",
        "v_snow" to "Videos de nieve y auroras", "v_deserts" to "Videos de desiertos", "v_nightsky" to "Videos del cielo nocturno",
        "v_fireplace" to "Chimenea", "v_calm" to "Colores relajantes",
        "v_scifi" to "Videos del espacio profundo", "v_gaming" to "Tráileres de videojuegos", "v_films" to "Cine libre",
        "gradient" to "Fondo oscuro"), "nature",
        summary = "Una foto de Bing por minuto o videos aéreos de Apple TV y Fire TV")
    val SHUFFLE = toggle("shuffle", "Escenas aleatorias")
    val DIM = Option("dim", "Oscurecer el fondo", listOf("low" to "Bajo", "medium" to "Medio", "high" to "Alto"), "medium")
    val SCENE_NAME = toggle("scene_name", "Nombre de la escena")

    // Behaviour
    val IDLE = Option("idle", "Mostrar el fondo tras",
        listOf("off" to "Nunca", "30" to "30 segundos", "60" to "1 minuto", "120" to "2 minutos", "300" to "5 minutos"), "60")
    val ALL_APPS = toggle("all_apps", "Fila de todas las apps", default = false)
    val ALL_APPS_TILE = toggle("all_apps_tile", "Acceso a todas las apps", summary = "Acceso a todas las apps al final de tus favoritos")

    // Movies & shows
    val SYNOPSIS_LANGUAGE = Option("synopsis_language", "Idioma de las sinopsis",
        listOf("es-MX" to "Español latino", "original" to "Original del catálogo"), "es-MX",
        summary = "Sinopsis de TMDB en español de México; conserva la original si no está disponible")
    val SUGGESTIONS = toggle("suggestions", "Filas de películas y series", summary = "Filas de tus complementos; OK abre el título en Nuvio")
    /** The TV tab with Nuvio+IPTV's channels (shown only when Nuvio+IPTV is installed). */
    val LIVE_TV = toggle("live_tv", "Pestaña de TV", true, "Tus canales de Nuvio+IPTV con vista previa en vivo")
    val BILLBOARD = toggle("billboard", "Carrusel destacado", summary = "Imágenes destacadas que rotan sobre las filas")
    val CONTINUE_WATCHING = toggle("continue_watching", "Seguir viendo", summary = "Desde tu cuenta de Nuvio")
    val GENRE = Option("genre", "Filtro de género", listOf("all" to "Todos los géneros", "Action" to "Acción", "Comedy" to "Comedia", "Drama" to "Drama",
        "Thriller" to "Suspenso", "Horror" to "Terror", "Romance" to "Romance", "Sci-Fi" to "Ciencia ficción", "Animation" to "Animación",
        "Crime" to "Crimen", "Family" to "Familiar"), "all", summary = "Solo en filas cuyo complemento lo permita")
    val RATING_BADGE = toggle("rating_badge", "Calificación en pósteres", summary = "Desactívala si los pósteres ya muestran una calificación")
    val TYPE_TABS = toggle("type_tabs", "Pestañas de contenido", summary = "Películas, Series y Anime; Inicio reúne todo")
    val POSTER_SIZE = Option("poster_size", "Tamaño del póster", listOf("small" to "Pequeño", "medium" to "Mediano", "large" to "Grande"), "medium")
    val BACKDROP = toggle("backdrop", "Fondo de la ficha", summary = "Muestra el arte del título seleccionado detrás de las filas")
    val TOP_TEN = Option("top_ten_from", "Fila de los 10 más populares",
        listOf("mine" to "De mi primera fila", "world" to "Mundial (mayormente en inglés)", "off" to "Desactivado"), "mine",
        summary = "Fila numerada con las diez películas más populares")
    val TRAILERS = toggle("trailers", "Reproducir tráileres", summary = "Se reproducen al mantener un póster seleccionado")
    val TRAILER_DELAY = Option("trailer_delay", "Iniciar el tráiler tras",
        listOf("1" to "1 segundo", "2" to "2 segundos", "3" to "3 segundos", "5" to "5 segundos", "8" to "8 segundos"), "2",
        summary = "Tiempo de selección antes de iniciar el tráiler")
    val TRAILER_SOUND = toggle("trailer_sound_v2", "Sonido del tráiler", summary = "Desactivado reproduce los tráileres sin sonido")


    private fun prefs(context: Context) = context.getSharedPreferences("flix_settings", Context.MODE_PRIVATE)

    fun get(context: Context, option: Option): String =
        prefs(context).getString(option.key, null)?.takeIf { id -> option.choices.any { it.first == id } } ?: option.default

    fun isOn(context: Context, option: Option) = get(context, option) == "on"

    fun set(context: Context, option: Option, id: String) {
        val p = prefs(context)
        p.edit().putString(option.key, id).putInt(KEY_REVISION, p.getInt(KEY_REVISION, 0) + 1).apply()
    }

    /** The city typed for the weather, or "" to locate the TV from its internet connection. */
    fun weatherCity(context: Context): String = prefs(context).getString("weather_city", null).orEmpty()

    fun setWeatherCity(context: Context, city: String) = setWeatherPlace(context, city, null)

    /** A place picked from the search list keeps its coordinates, so the right town of that name is used. */
    fun setWeatherPlace(context: Context, city: String, coords: Pair<Double, Double>?) {
        val p = prefs(context)
        p.edit().putString("weather_city", city.trim())
            .apply { if (coords == null) remove("weather_lat").remove("weather_lon")
                     else putString("weather_lat", coords.first.toString()).putString("weather_lon", coords.second.toString()) }
            .putInt(KEY_REVISION, p.getInt(KEY_REVISION, 0) + 1).apply()
    }

    fun weatherCoords(context: Context): Pair<Double, Double>? = prefs(context).let { p ->
        val lat = p.getString("weather_lat", null)?.toDoubleOrNull()
        val lon = p.getString("weather_lon", null)?.toDoubleOrNull()
        if (lat != null && lon != null) lat to lon else null
    }

    /** Bumped on every change, so the home screen knows when to re-apply. */
    fun revision(context: Context) = prefs(context).getInt(KEY_REVISION, 0)

    private const val KEY_REVISION = "revision"
}
