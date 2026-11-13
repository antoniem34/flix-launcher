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

## Pestaña Anime — latino4

La versión `1.0.23-latino4` (código 27) sustituye la pestaña superior **Apps** por **Anime**, junto a Películas y Series. Muestra las filas habilitadas de Kitsu y un carrusel de anime, con películas y series de Kitsu. No incluye el Top 10 de películas ni la fila general Seguir viendo. Inicio conserva la vista conjunta; Películas y Series mantienen sus catálogos y dejan de mezclar los destacados de Kitsu. Todas las apps continúa disponible desde su acceso en la fila de favoritos.

El filtro se aplica tanto al cambiar de pestaña como al recibir catálogos en segundo plano o reconstruir las filas. Cambiar de pestaña respeta la preferencia guardada de mostrar u ocultar la fila de aplicaciones. La opción existente `type_tabs`, ahora llamada **Pestañas de contenido**, controla Películas, Series y Anime sin modificar su valor guardado. Se conservan los estilos, las filas, su orden y las preferencias; no hay migración ni restablecimiento de datos.

### Validación de latino4 — 13 de noviembre de 2026

- Las nuevas pruebas detectaron tres fallos con el filtro anterior. Tras corregirlo, aprobaron las 17 pruebas JVM: cuatro del filtro, cinco de Kitsu, dos de búsqueda y seis de sinopsis.
- APK release compilada con la misma firma e instalada sobre latino3 sin borrar datos. Lint conserva los 13 errores y 161 avisos anteriores; no hay errores nuevos ni se han ocultado los existentes.
- En ONN Android 14 se recorrieron las cuatro filas de Anime, con sus sinopsis en español. Películas y Series muestran sus catálogos, Todas las apps abre Biblioteca y el regreso con Inicio funciona. Se conserva el diseño sin solapamientos y la fila adicional de apps permanece oculta según la preferencia guardada.
- Los respaldos de las 11:31 y 11:45 conservan exactamente todas las preferencias, los favoritos, las imágenes de perfil y las siete filas con su orden. Solo cambia la marca interna `home_service_rebinding`.
- Servicio Inicio activo, sin aviso rojo y buffer de crashes vacío. Navegación comprobada por ADB; no se repitieron reproducción, reinicio, mando físico ni los enlaces a Nuvio ya verificados en latino3.

## Categorías de anime e Inicio reducido — latino5

La versión `1.0.23-latino5` (código 28) muestra en Inicio únicamente **Anime en emisión** entre las filas de Kitsu. Las demás filas habilitadas siguen disponibles en Anime. El carrusel también respeta la procedencia de las filas: en Inicio solo incorpora anime de la fila en emisión, además de los catálogos habituales de películas y series.

**Complementos y filas → Agregar categorías de anime** añade siete filas temáticas de Kitsu: Mundo Isekai, Magia y hechicería, Mechas y robots, Vida escolar, Juegos y mundos virtuales, Samuráis y Sobrenatural. Se consultan los filtros reales `Isekai`, `Magic`, `Mecha`, `School`, `Game`, `Samurai` y `Supernatural` del catálogo popular. No se reclasifican títulos por su nombre ni se sustituyen por una lista genérica. El 13 de noviembre, Isekai devolvía tres títulos etiquetados; las otras seis consultas devolvían 20 títulos cada una. La cobertura depende de las etiquetas del proveedor. No se añaden estaciones/años: el complemento no publica ese filtro.

La acción añade solo las categorías ausentes y conserva filas desactivadas, nombres y orden anteriores. Las categorías pueden ocultarse, ordenarse o eliminarse con los controles existentes. Sus filtros son fijos y no se reemplazan por el filtro global de género. La identidad de las filas y su caché incluyen ahora la consulta extra para evitar que Isekai, magia y otros temas compartan resultados; las claves de las filas sin consulta extra siguen iguales. No se cambia ninguna preferencia de apariencia, sinopsis ni aplicaciones.

Referencias del proveedor: [manifiesto](https://anime-kitsu.strem.fun/manifest.json), [controlador de catálogos](https://github.com/TheBeastLT/stremio-kitsu-anime/blob/master/addon.js), [consulta real Isekai](https://anime-kitsu.strem.fun/catalog/anime/kitsu-anime-popular/genre=Isekai.json).

### Validación de latino5 — 13 de noviembre de 2026

- Dos pruebas reprodujeron la mezcla de filas en Inicio y la colisión de identidad de categorías. Las 21 pruebas JVM finales aprueban, incluidas la conservación de claves originales y la adición repetida sin duplicar o reactivar categorías deshabilitadas.
- Release compilada, firma conservada y actualización instalada sobre latino4. Lint mantiene los 13 errores y 161 avisos anteriores, sin errores nuevos.
- Las siete consultas se comprobaron contra el proveedor: todos los títulos devueltos contenían la etiqueta solicitada. En el ONN se recorrieron las siete categorías; Mundo Isekai muestra tres títulos y la sinopsis española de Slime. Mechas, Juegos y Sobrenatural también mostraron sus sinopsis en español.
- Inicio conserva Top 10 y las tres filas de Cinemeta, seguidas únicamente de Anime en emisión. La pestaña Anime contiene las cuatro filas originales y las siete temáticas. El botón nuevo añadió siete filas desde la interfaz de la TV.
- Los respaldos de las 11:52 y 12:02 conservan exactamente los ajustes, favoritos, imágenes de perfil y las siete filas anteriores con su orden. Solo cambian la lista de filas (siete añadidas al final) y la marca interna del servicio Inicio.
- Servicio Inicio activo, buffer de crashes vacío y TV dejada en Anime. Navegación por ADB; no se repitieron reproducción, reinicio ni pruebas con mando físico.

## Servidor local de Nuvio — latino6

«Seguir viendo» consulta los proveedores del perfil seleccionado para recuperar los títulos, carátulas y descripciones de identificadores propios como AnimES y Donghua. No importa sus catálogos ni cambia las filas de Inicio. La caché de metadatos distingue servidor, sesión, perfil y proveedores.

Al abrir un episodio se conserva el `video_id` exacto del historial, incluido Kitsu, AnimES y Pluto. Para «A continuación» se obtiene el identificador del episodio siguiente de la lista de videos del proveedor. Cuando falta un identificador válido de un proveedor personalizado, se abre la ficha en lugar de fabricar un ID de reproducción. La preferencia de sinopsis se conserva; los proveedores con IDs propios mantienen su descripción original.

Para IDs Kitsu se consulta primero su proveedor original: otros complementos pueden responder al mismo ID omitiendo la referencia IMDb necesaria para la sinopsis en español. El orden efectivo de proveedores forma parte de la clave de caché, evitando conservar metadatos anteriores sin esa referencia. Cuando no existe traducción disponible se conserva el aviso, sin generar una sinopsis.

La versión `1.0.23-latino6` (código 29) permite elegir **Servidor local** o **Nuvio en la nube** en Ajustes → Películas y series → Nuvio. Para un servidor propio se introduce su dirección base; Flix comprueba `/.well-known/nuvio`, la identidad del servicio, su dirección anunciada y los métodos de acceso disponibles. Usa la clave pública anunciada, sin pedir claves administrativas. Se conserva el acceso por QR/código y por correo/contraseña cuando el servidor los admite.

Las sesiones cifradas, perfiles elegidos y cachés de Seguir viendo quedan separadas por servidor. La nube conserva su archivo de sesión anterior. Las peticiones iniciadas quedan vinculadas a su servidor y los resultados atrasados de otro perfil se descartan. Las credenciales locales no se envían al servicio externo Nuvio+IPTV. La autenticación no sigue redirecciones HTTP hacia otro destino.

La conexión local usa los perfiles y el progreso existentes, sin fusionarlos ni importar complementos automáticamente. Las filas, favoritos y ajustes de Flix se conservan. El selector de perfil de la cabecera cambia el historial que muestra el launcher; abrir un título sigue delegando la reproducción a Nuvio. Los respaldos de ajustes no contienen tokens de acceso.

Referencia: [configuración oficial de clientes Nuvio](https://github.com/NuvioMedia/self-host/blob/main/docs/client-configuration.md). Para QR, la página `/tv-login` del servidor debe estar disponible y usar el mismo backend.

### Validación de latino6 — 13 de noviembre de 2026

- 33 pruebas JVM aprobadas. Los casos de regresión reproducen la falta de proveedores del perfil, la pérdida del ID exacto del episodio y la prioridad incorrecta de metadatos Kitsu; también cubren descubrimiento, separación de servidor y rechazo de direcciones ambiguas.
- Release compilada, firma conservada e instalada por actualización. Lint sigue sin aprobar: los mismos 13 errores preexistentes y 164 avisos (tres avisos nuevos `SetTextI18n` en la interfaz de conexión, consistente con los literales del proyecto).
- Autenticación local por código aprobada por el usuario y conservada tras actualizar la APK. En ONN, Inicio muestra nombres de Frieren, Angolmois, KonoSuba, Mago cuerpo a cuerpo, Psyren y A Record Mortal's Journey To Immortality en lugar de sus identificadores técnicos.
- Apertura real de Psyren desde Flix: tras elegir AnimES en Nuvio, la sesión multimedia confirma reproducción de S1:E4 desde el punto guardado; se pausó a los 799028 ms. La reproducción automática avanzó unos segundos durante la prueba. Las capturas del reproductor resultan negras, por lo que no se afirma verificación visual del video ni del audio.
- Se cambió a CineSeriES y se comprobó su historial distinto, luego se volvió a AnimES. Algunos títulos de CineSeriES aún carecen de metadatos del proveedor; no se atribuye cobertura completa a todos los complementos.
- Tras la actualización final, Frieren muestra su sinopsis española, T1 E1 y 5 % de progreso en Seguir viendo. Se conserva la sesión local; TV dejada en AnimES y buffer de crashes vacío.
- Respaldos de las 13:11 y 13:13 comparados con el de las 12:17: preferencias, favoritos, imágenes de perfil y las 14 filas idénticos; solo cambia la marca interna `home_service_rebinding`. No se restauraron ajustes.

## Catálogos locales sin Cinemeta — latino7

La versión `1.0.23-latino7` (código 30) admite manifiestos HTTPS y HTTP de la red local, conserva rutas de configuración codificadas y no convierte HTTP a HTTPS. Importa los catálogos navegables de los complementos locales habilitados en el perfil de Nuvio. Los catálogos que requieren una búsqueda se consultan desde Buscar, sin crear una fila vacía para ellos.

Cada perfil local guarda sus filas y su orden por servidor, cuenta y perfil; no se fusionan perfiles, historiales ni complementos de Nuvio. La sincronización añade únicamente filas ausentes y respeta las que el usuario ocultó u ordenó. En la primera importación se activan las filas principales del proveedor (emisión, populares y siguiendo), y los catálogos individuales de Donghua/LACartoons/Danimados; las demás quedan disponibles en Complementos y filas. Las filas Kitsu anteriores se conservan en los perfiles con proveedores de anime. El botón «Sincronizar catálogos de este perfil» permite actualizar el listado.

Anime admite AnimES y los proveedores Donghua, además de Kitsu. Inicio conserva solo la emisión entre esas filas de anime y da prioridad a la de AnimES si está habilitada; la fila Kitsu permanece guardada y disponible en Anime. No se cambia la distribución visual, el tema, los tamaños, la animación, los favoritos ni la preferencia de sinopsis.

Cinemeta se elimina de las filas heredadas y de las consultas directas de búsqueda, metadatos de Seguir viendo, Top 10, tráileres y fondos de películas. Top 10 toma el orden de la primera fila de películas habilitada, sin presentar ese orden como un ranking mundial. El valor anterior `world` se conserva en las preferencias, pero usa los catálogos configurados. Los fondos utilizan sus imágenes de fondo; los tráileres, los metadatos de su proveedor. El identificador antiguo de Cinemeta solo se conserva para reconocerlo y bloquearlo al importar o restaurar ajustes.

Los servicios externos que tus addons usen internamente no se modifican. Kitsu y las sinopsis es-MX siguen disponibles con el comportamiento anterior. La reproducción continúa en Nuvio, conservando los identificadores y enlaces locales del proveedor.

Los filtros de género usan las opciones publicadas por cada manifiesto: por ejemplo, la selección guardada `Action` se envía como `Acción` a AnimES. Si ese complemento no ofrece el género seleccionado, no se envía un filtro inválido. Las filas con nombres repetidos muestran el proveedor para distinguirlas. Danimados anuncia búsqueda pero su endpoint devuelve una lista fija ajena al término; para ese catálogo local Flix filtra los nombres de su catálogo completo, sin modificar el servicio ni sus IDs.

### Validación de latino7 — 13 de noviembre de 2026

- 42 pruebas JVM aprobadas. Se reprodujeron antes de corregirlos los fallos de clasificación de filas locales, identidad de anime, dependencia de Cinemeta para IMDb, búsqueda de Danimados y nombres de géneros. Release compilada y firmada con la clave existente; actualización en ONN sin borrar datos. Lint conserva los mismos 13 errores y 164 avisos previos, sin nuevos errores.
- El usuario creó su perfil completo Antoni durante la prueba. Se importaron sus 62 catálogos locales navegables: AnimES 28, CineSeriES 14, CartoonES 16 y uno de Danimados, DonghuaLife, MundoDonghua y LACartoons. Se conservaron las 11 filas Kitsu: 73 disponibles, 26 activadas. Los catálogos exclusivos de búsqueda se omiten de las filas, pero participan en Buscar.
- Se verificó aislamiento entre Antoni y AnimES: este último conserva 41 filas disponibles y 15 activas de sus proveedores de anime, sin las filas de cine y cartoons de Antoni. No se fusionaron ni modificaron perfiles o addons en el servidor Nuvio.
- En ONN se comprobaron En emisión de AnimES, películas de CineSeriES/CartoonES, Anime, búsquedas de Frieren y Phineas, y apertura de Battle Spirits desde su catálogo local en la ficha correcta de Nuvio. La búsqueda de Frieren ya no muestra títulos ajenos de Danimados. No se inició reproducción en esta prueba.
- Respaldo de las 14:47 frente al de las 14:21: ajustes de apariencia/sinopsis, favoritos, imágenes de perfil y opciones originales de las 11 filas Kitsu conservados. Cambian las filas de catálogos y la marca interna del servicio Inicio. Antoni quedó seleccionado. El servicio Inicio sigue habilitado; no hubo crashes durante estas pruebas. El buffer retiene un crash de RecyclerView de las 14:05, anterior a la tarea y a latino7.

## Inicio y pestañas por perfil — latino8

En Complementos y filas, «Inicio de este perfil» permite elegir «Solo Seguir viendo y Anime en emisión». La elección se guarda junto al ámbito de catálogos del perfil, sin modificar sus filas activas, orden, favoritos ni ajustes visuales. Los demás perfiles conservan el comportamiento anterior por defecto; la opción forma parte del respaldo habitual.

Con esta opción, Inicio muestra únicamente Seguir viendo y la fila de anime en emisión (AnimES tiene prioridad sobre Kitsu). El carrusel destacado y Top 10 no aparecen en Inicio. Películas conserva las filas de películas de CineSeriES/CartoonES; Series conserva sus filas de series y las de cartoons; Anime reúne AnimES, Kitsu y donghua. Seguir viendo queda solo en Inicio. Los destacados continúan en las tres pestañas y se conserva el acceso a las apps favoritas. Las filas disponibles pero desactivadas siguen desactivadas.

El mismo filtro se aplica al cambiar de pestaña y cuando terminan de cargar las filas en segundo plano. La opción participa en la detección de cambios para reconstruir la vista inmediatamente al volver de los ajustes, sin reiniciar ni restaurar datos.

En Antoni se conserva la selección de 26 catálogos activos: cuatro de películas, siete de series/cartoons y quince de anime (incluido donghua). Top 10, si estaba activado, permanece en Películas. La fila de anime en emisión también permanece en Anime. No se activan automáticamente los otros catálogos disponibles.

Validación del 13 de noviembre de 2026: 45 pruebas JVM aprobadas, incluidos dos casos que fallaban antes del cambio. Release instalada en ONN con la misma firma y sin borrar datos. Se verificaron Inicio, su última fila, las tres pestañas, los destacados de catálogo sin historial fijado y la persistencia de la opción al reiniciar el proceso mediante actualización. Lint conserva los mismos 13 errores/164 avisos heredados. No hubo nuevos crashes durante la prueba.

Respaldo de las 15:07 comparado con el de las 14:54: `flix_settings`, favoritos, avatares y todos los catálogos (activación y orden incluidos) idénticos. Solo se añadió `compact_home_catalogue_rows_local_00847e18c45dcaa85103cb830bedf11c930573c0=true` para Antoni y avanzaron las marcas internas `home_service_rebinding` y `housekeeping_at`. No se restauraron datos ni se cambiaron perfiles, complementos o historial de Nuvio. APK final latino8, código 31, SHA-256 `ac99bf795694d8adba937aee8303afc485a82729630147591ab6edb5cea9c99c`.

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
