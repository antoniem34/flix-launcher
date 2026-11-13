# Flix Latino

Fork de [HatriGt/flix-launcher](https://github.com/HatriGt/flix-launcher), basado en v1.0.23. Conserva la licencia MIT y los créditos originales.

## Sinopsis

En **Ajustes → Películas y series → Idioma de las sinopsis** se puede elegir **Español latino** (predeterminado) u **Original del catálogo**.

Los destacados y las fichas de películas/series solicitan únicamente la descripción en `es-MX` a [TMDB Addon](https://github.com/mrcanelas/tmdb-addon), alojado en `https://tmdb.elfhosted.com`. Se envían el tipo y el identificador público IMDb/TMDB; no se envían credenciales ni direcciones de addons. La calidad y disponibilidad de la traducción dependen del proveedor, que puede devolver su propio idioma de respaldo.

La consulta se hace después de detener la navegación, en segundo plano. Las respuestas tardías no sustituyen la descripción de otra ficha. La caché dura siete días; si falla la red se conserva la traducción anterior o el texto original y se espera quince minutos antes de reintentar esa ficha. Los identificadores propios de otros addons mantienen su descripción original, excepto el tratamiento de Kitsu descrito abajo. Las respuestas de metadatos reutilizan los límites y escrituras atómicas del proyecto.

No se alteran títulos, imágenes, orden de catálogos, identificadores, enlaces a Nuvio, audio ni subtítulos. No se requiere una clave API personal ni una cuenta nueva.

## Instalación y actualizaciones

- Identificador independiente: `com.flix.launcher.latino` (nombre visible: **Flix Latino**).
- Versión inicial: `1.0.23-latino1`, código 24.
- Puede convivir con la versión oficial: sus firmas y datos son independientes.
- Exportar ajustes desde la app oficial en **Settings → About → Back up settings**. Copiar ese JSON a `Android/data/com.flix.launcher.latino/files/backups` y restaurar desde el mismo menú de Flix Latino.
- El respaldo original no incluye el inicio de sesión de Nuvio. Si se utilizaba una cuenta dentro del launcher, hay que volver a iniciarla en la nueva app.
- Las actualizaciones automáticas oficiales están desactivadas para este fork; se actualiza instalando otra APK del fork firmada con la misma clave.
- En Google TV, habilitar **Botón Inicio de Flix Latino** en Accesibilidad y desactivar el servicio de Inicio de la versión anterior. No es necesario deshabilitar el launcher de Google.

## Interfaz en español — latino2

La versión `1.0.23-latino2` (código 25) traduce los menús, ajustes, biblioteca de apps, búsquedas, complementos, perfiles, TV en vivo, ayuda, avisos y control desde el teléfono. Las fechas y los días usan `es-MX`; el clima solicita descripciones en español. La búsqueda de ajustes admite palabras con o sin acentos.

Se conservan las dimensiones, colores, tipografías, distribución y comportamiento de navegación. Las claves de preferencias, IDs de opciones, géneros de consulta, rutas y datos de respaldo mantienen sus valores originales. Los títulos de películas y apps y los nombres personalizados de catálogos se conservan; las etiquetas conocidas de Cinemeta y géneros se traducen solo al mostrarlas. Los textos suministrados por proveedores externos pueden mantener su idioma de origen.

La actualización se instala sobre la misma aplicación, con la misma firma y sin borrar datos. En Flix Latino, los respaldos se exportan en **Ajustes → Acerca de → Respaldar ajustes**.

### Comprobaciones de latino2 — 13 de noviembre de 2026

- Ocho pruebas JVM aprobadas: las seis de sinopsis y dos de búsqueda sin acentos y conservación de nombres externos.
- Las 96 opciones de selección y 36 claves explícitas de ajustes conservan sus IDs; el XML principal solo cambia textos y descripciones de accesibilidad.
- Lint completo sigue sin aprobar: los mismos 13 errores de latino1 y 161 avisos. El aviso adicional `SetTextI18n` señala un texto de TV en vivo traducido con el mismo estilo de literales que utiliza el proyecto; no se migró toda la interfaz a recursos Android ni se ocultaron hallazgos.
- `assembleRelease` completado y firma comprobada. Actualización instalada con `adb install -r` en ONN Android 14, sin desinstalar ni borrar datos.
- Se revisaron Inicio, ajustes, biblioteca, menú de una app y complementos sin solapamientos. La búsqueda encuentra «peliculas» sin acento y «complementos». El botón Inicio regresó desde Ajustes de Android a Flix Latino, con el servicio activo y sin aviso rojo. El buffer de crashes consultado quedó vacío. No se probaron un reinicio, el mando físico ni reproducción.

## Anime Kitsu — latino3

La versión `1.0.23-latino3` (código 26) admite las fichas de [Anime Kitsu](https://github.com/TheBeastLT/stremio-kitsu-anime). Se agrega en **Ajustes → Películas y series → Complementos y filas → Agregar complemento por URL** con `https://anime-kitsu.strem.fun/manifest.json`. Sus cuatro filas se muestran en Inicio y se pueden ordenar o desactivar desde el mismo panel: Anime en tendencia, Anime en emisión, Anime más popular y Anime mejor valorado. El catálogo también participa en la búsqueda existente. No se sustituye el catálogo de películas/series ni se cambian ajustes de apariencia.

Cada ficha conserva su ID `kitsu:` aunque el proveedor incluya un IMDb compartido con otras temporadas. IMDb solo se utiliza como equivalencia explícita para consultar la sinopsis `es-MX`; no se hacen coincidencias aproximadas por nombre. En series, la descripción se identifica como **Sinopsis general de la serie**, porque no es una traducción específica de cada temporada de Kitsu. Si no existe equivalencia o la consulta falla sin caché, se muestra **Sinopsis en español latino no disponible para este anime**. La opción **Original del catálogo** sigue mostrando el texto original. No se garantiza cobertura total ni la variante regional de cada texto suministrado por el proveedor.

La apertura en Nuvio usa la ficha `kitsu:` y la URL del complemento, sin fabricar números de temporada/episodio de IMDb. Kitsu aporta catálogo y metadatos; las fuentes de reproducción se configuran en Nuvio. Las puntuaciones aportadas por Kitsu llevan su etiqueta en lugar de atribuirse a IMDb.

### Validación de latino3 — 13 de noviembre de 2026

- La prueba de dos temporadas con IMDb compartido falló antes del cambio, al igual que la identidad de una película de anime. Después del cambio, las 13 pruebas JVM aprobaron: identidad y enlaces de Kitsu, equivalencias inválidas, etiquetas, búsquedas y caché de sinopsis.
- APK release compilada y firma conservada. Lint mantiene los 13 errores y 161 avisos de latino2, sin errores nuevos.
- ONN Android 14 actualizado sin borrar datos. Se añadieron cuatro filas después de las tres de Cinemeta. El respaldo posterior conserva exactamente todas las preferencias de `flix_settings`, los favoritos y las imágenes de perfil de Flix; solo cambian las filas y la marca interna de recuperación del servicio Inicio.
- En la TV: Ataque a los Titanes y My Hero Academia 2 muestran sinopsis generales en español. Sus enlaces abren las fichas correctas en Nuvio, incluida la segunda temporada de My Hero Academia (ID `kitsu:12268`, año 2017), tras seleccionar un perfil. La búsqueda «naruto» devuelve series y películas de Kitsu. Las puntuaciones llevan la etiqueta Kitsu.
- Inicio regresa a Flix Latino desde Nuvio, con su servicio activo y sin aviso rojo. Buffer de crashes vacío. No se inició reproducción ni se probó un reinicio o el mando físico: la navegación se envió por ADB.

## Compilación de prueba

JDK 17, SDK Android 35 y el wrapper Gradle incluido. `local.properties` contiene solamente la ruta local del SDK y, si procede, las credenciales locales de firma; está excluido de Git.

```powershell
./gradlew.bat -PallowDebugSigning --no-daemon --max-workers=2 testReleaseUnitTest lintRelease assembleRelease
```

`allowDebugSigning` produce una APK de prueba con la clave debug local; conservar esa clave para futuras actualizaciones de esa instalación. Para distribuir releases, usar los parámetros de firma `flix.*` ya previstos por el proyecto. No subir claves, respaldos de ajustes ni APK al árbol Git.

Las pruebas unitarias cubren tipos/IDs admitidos, metadatos ausentes, caché persistente, recuperación sin red y reintentos. La comprobación real en TV debe incluir películas, series, navegación rápida, apertura en Nuvio y regreso con Inicio.

## Validación de latino1 — 13 de noviembre de 2026

- `testReleaseUnitTest`: seis pruebas aprobadas, sin fallos ni errores.
- `assembleRelease`: compilación y firma verificadas.
- `lintRelease`: no aprobado; 13 errores y 160 avisos. Los errores señalan líneas sin cambios respecto a v1.0.23: APIs 30 en Trailer/TrailerService (minSdk 28), constante de registro del receptor en MainActivity y orientación de un LinearLayout del layout original. Ningún hallazgo en Synopsis/SynopsisSource. No se ocultaron mediante una baseline ni se amplió este cambio para corregirlos.
- ONN 4K con Android 14: instalación independiente, importación del respaldo, favoritos conservados, película destacada y serie con sinopsis en español, navegación rápida entre series, cambio a idioma original y vuelta a español comprobados.
- Nuvio abrió su selector de perfiles; no se inició reproducción. Inicio regresó a Flix Latino mediante su servicio de accesibilidad, sin aviso rojo. Se concedió WRITE_SECURE_SETTINGS para la recuperación del servicio prevista por HomeGuard. No se probó un reinicio ni el mando físico; las pulsaciones se enviaron por ADB.
