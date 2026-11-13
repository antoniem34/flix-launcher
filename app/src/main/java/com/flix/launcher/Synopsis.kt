package com.flix.launcher

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/** Bind on the UI thread; slow responses can never replace a newer title's description. */
internal object Synopsis {
    private val worker = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())
    private var source: SynopsisSource? = null // accessed only by worker

    fun bind(view: TextView, title: Catalog.Title) {
        (view.getTag(R.id.synopsis_request) as? AtomicBoolean)?.set(false)
        val active = AtomicBoolean(true)
        view.setTag(R.id.synopsis_request, active)
        val latino = FlixSettings.get(view.context, FlixSettings.SYNOPSIS_LANGUAGE) == "es-MX"
        val synopsisId = title.synopsisId
        val supported = synopsisId != null && SynopsisSource.endpoint(title.type, synopsisId) != null
        view.text = if (latino && title.isKitsu) {
            if (supported) "Cargando sinopsis en español latino…" else UNAVAILABLE
        } else title.description
        if (!latino || !supported || synopsisId == null) return
        val target = WeakReference(view)
        val app = view.context.applicationContext
        // Debounce remote-control scrolling and skip work for views already replaced/hidden.
        view.postDelayed({
            if (!active.get() || !view.isShown || !view.isAttachedToWindow) return@postDelayed
            worker.execute {
                if (!active.get()) return@execute
                val repository = source ?: SynopsisSource(File(app.cacheDir, "synopsis-es-MX"))
                    .also { source = it }
                val translated = repository.load(title.type, synopsisId)
                val text = when {
                    translated != null && title.isKitsu && title.type == "series" -> "Sinopsis general de la serie: $translated"
                    translated != null -> translated
                    title.isKitsu -> UNAVAILABLE
                    else -> return@execute
                }
                main.post {
                    val current = target.get() ?: return@post
                    if (active.get() && current.isAttachedToWindow &&
                        current.getTag(R.id.synopsis_request) === active &&
                        FlixSettings.get(app, FlixSettings.SYNOPSIS_LANGUAGE) == "es-MX") current.text = text
                }
            }
        }, 350)
    }

    private const val UNAVAILABLE = "Sinopsis en español latino no disponible para este anime."
}
