package com.flix.launcher

import org.json.JSONObject
import java.net.URI
import java.util.Locale

/** Connection identity, independent of Android and of account credentials. */
data class NuvioBackend private constructor(val url: String) {
    val isCloud get() = url == CLOUD_URL
    val preferenceName get() = if (isCloud) "nuvio_account" else "nuvio_account_${Catalog.hashName(url)}"
    val loginUrl get() = if (isCloud) "https://nuvio.tv/tv-login" else "$url/tv-login"
    val cachePrefix get() = if (isCloud) "continue_" else "continue_local_${Catalog.hashName(url)}_"

    data class Discovery(val backend: NuvioBackend, val key: String, val tvLogin: Boolean, val passwordLogin: Boolean)

    companion object {
        const val CLOUD_URL = "https://api.nuvio.tv"
        val CLOUD = NuvioBackend(CLOUD_URL)
        fun from(value: String): NuvioBackend {
            val uri = runCatching { URI(value.trim()) }.getOrElse { throw IllegalArgumentException("Dirección de servidor inválida") }
            val scheme = uri.scheme?.lowercase(Locale.ROOT)
            val host = uri.host?.lowercase(Locale.ROOT)
            require(scheme in listOf("https", "http") && !host.isNullOrBlank() &&
                uri.userInfo == null && uri.query == null && uri.fragment == null && uri.port in -1..65535 && uri.port != 0) {
                "Usa la dirección http:// o https:// del servidor, sin usuario, contraseña ni parámetros"
            }
            val octets = host.split('.').mapNotNull { it.toIntOrNull()?.takeIf { n -> n in 0..255 } }
            val privateIp = host.split('.').size == 4 && octets.size == 4 && (octets[0] == 10 || octets[0] == 127 ||
                octets[0] == 192 && octets[1] == 168 || octets[0] == 172 && octets[1] in 16..31)
            require(scheme == "https" || privateIp || host == "localhost" || host.endsWith(".local") || host == "[::1]") {
                "HTTP solo está permitido para un servidor en tu red local"
            }
            val port = uri.port.takeUnless { it == 443 && scheme == "https" || it == 80 && scheme == "http" } ?: -1
            val path = uri.path.orEmpty().trimEnd('/')
            require(!path.contains("..")) { "Usa la dirección base del servidor" }
            return NuvioBackend(URI(scheme, null, host, port, path, null, null).toASCIIString())
        }
        fun discover(requested: NuvioBackend, json: String): Discovery {
            val data = JSONObject(json)
            require(data.optInt("version") == 1 && data.optString("service") == "nuvio" && data.optBoolean("self_hosted")) {
                "El servidor no anuncia una conexión Nuvio compatible"
            }
            val backend = from(data.getString("backend_url"))
            require(backend == requested) { "El servidor anuncia otra dirección; introduce su dirección base correcta" }
            val key = data.optString("publishable_key")
            require(key.isNotBlank() && key != "null") { "El servidor no proporcionó su clave pública de conexión" }
            val capabilities = data.optJSONObject("capabilities") ?: JSONObject()
            val tv = capabilities.optBoolean("tv_login")
            val password = capabilities.optBoolean("email_password_auth")
            require(tv || password) { "El servidor no ofrece un método de inicio de sesión compatible" }
            return Discovery(backend, key, tv, password)
        }
    }
}
