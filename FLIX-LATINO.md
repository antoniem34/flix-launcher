# Flix Latino

Fork de [HatriGt/flix-launcher](https://github.com/HatriGt/flix-launcher), basado en v1.0.23. Conserva la licencia MIT y los créditos originales.

## Sinopsis

En **Settings → Movies & shows → Idioma de las sinopsis** se puede elegir **Español latino** (predeterminado) u **Original del catálogo**.

Los destacados y las fichas de películas/series solicitan únicamente la descripción en `es-MX` a [TMDB Addon](https://github.com/mrcanelas/tmdb-addon), alojado en `https://tmdb.elfhosted.com`. Se envían el tipo y el identificador público IMDb/TMDB; no se envían credenciales ni direcciones de addons. La calidad y disponibilidad de la traducción dependen del proveedor, que puede devolver su propio idioma de respaldo.

La consulta se hace después de detener la navegación, en segundo plano. Las respuestas tardías no sustituyen la descripción de otra ficha. La caché dura siete días; si falla la red se conserva la traducción anterior o el texto original y se espera quince minutos antes de reintentar esa ficha. Los identificadores propios de otros addons mantienen su descripción original. Las respuestas de metadatos reutilizan los límites y escrituras atómicas del proyecto.

No se alteran títulos, imágenes, orden de catálogos, identificadores, enlaces a Nuvio, audio ni subtítulos. No se requiere una clave API personal ni una cuenta nueva.

## Instalación y actualizaciones

- Identificador independiente: `com.flix.launcher.latino` (nombre visible: **Flix Latino**).
- Versión inicial: `1.0.23-latino1`, código 24.
- Puede convivir con la versión oficial: sus firmas y datos son independientes.
- Exportar ajustes desde la app oficial en **Settings → About → Back up settings**. Copiar ese JSON a `Android/data/com.flix.launcher.latino/files/backups` y restaurar desde el mismo menú de Flix Latino.
- El respaldo original no incluye el inicio de sesión de Nuvio. Si se utilizaba una cuenta dentro del launcher, hay que volver a iniciarla en la nueva app.
- Las actualizaciones automáticas oficiales están desactivadas para este fork; se actualiza instalando otra APK del fork firmada con la misma clave.
- En Google TV, habilitar **Flix Latino Home button** en Accesibilidad y desactivar el servicio de Inicio de la versión anterior. No es necesario deshabilitar el launcher de Google.

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
