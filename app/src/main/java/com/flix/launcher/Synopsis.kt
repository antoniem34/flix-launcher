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
        view.text = title.description
        if (FlixSettings.get(view.context, FlixSettings.SYNOPSIS_LANGUAGE) != "es-MX" ||
            SynopsisSource.endpoint(title.type, title.id) == null) return
        val target = WeakReference(view)
        val app = view.context.applicationContext
        // Debounce remote-control scrolling and skip work for views already replaced/hidden.
        view.postDelayed({
            if (!active.get() || !view.isShown || !view.isAttachedToWindow) return@postDelayed
            worker.execute {
                if (!active.get()) return@execute
                val repository = source ?: SynopsisSource(File(app.cacheDir, "synopsis-es-MX"))
                    .also { source = it }
                val text = repository.load(title.type, title.id) ?: return@execute
                main.post {
                    val current = target.get() ?: return@post
                    if (active.get() && current.isAttachedToWindow &&
                        current.getTag(R.id.synopsis_request) === active &&
                        FlixSettings.get(app, FlixSettings.SYNOPSIS_LANGUAGE) == "es-MX") current.text = text
                }
            }
        }, 350)
    }
}
