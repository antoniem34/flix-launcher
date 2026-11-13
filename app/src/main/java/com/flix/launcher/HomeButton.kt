package com.flix.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

/**
 * Where the Home button goes, and the right way to point it at Flix on this TV.
 *
 * Plain Android TV follows the default home app, so choosing Flix there is enough. Google TV
 * ignores the default and needs [HomeKeyService]. Some TVs (Android 13+, apps installed from an
 * APK) refuse to start that service: it is switched on in Settings but never runs.
 */
object HomeButton {
    enum class State { SERVICE, DEFAULT_HOME, BLOCKED, OFF }

    private const val GOOGLE_TV_HOME = "com.google.android.apps.tv.launcherx"

    /** The fix shown to people whose TV blocks the service; needs a computer once. */
    const val UNBLOCK_COMMAND = "adb shell appops set com.flix.launcher.latino ACCESS_RESTRICTED_SETTINGS allow"
    const val DEFAULT_COMMAND = "adb shell cmd package set-home-activity com.flix.launcher.latino/com.flix.launcher.MainActivity"

    fun state(context: Context): State = when {
        HomeKeyService.running -> State.SERVICE
        isDefaultHome(context) && !isGoogleTv(context) -> State.DEFAULT_HOME
        serviceSwitchedOn(context) -> State.BLOCKED
        else -> State.OFF
    }

    fun works(context: Context) = state(context).let { it == State.SERVICE || it == State.DEFAULT_HOME }

    /** Google TV sends Home to its own screen whatever the default is. */
    fun isGoogleTv(context: Context) = runCatching { context.packageManager.getPackageInfo(GOOGLE_TV_HOME, 0); true }.getOrDefault(false)

    fun isDefaultHome(context: Context): Boolean = runCatching {
        context.packageManager.resolveActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), PackageManager.MATCH_DEFAULT_ONLY)
            ?.activityInfo?.packageName == context.packageName
    }.getOrDefault(false)

    private fun serviceSwitchedOn(context: Context): Boolean {
        val me = ComponentName(context, HomeKeyService::class.java)
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
            .split(':').any { ComponentName.unflattenFromString(it) == me }
    }

    /** One line for Settings and bug reports. */
    fun describe(context: Context): String = when (state(context)) {
        State.SERVICE -> "Funciona: el servicio de Inicio de Flix está activo"
        State.DEFAULT_HOME -> "Funciona: Flix es la app de inicio"
        State.BLOCKED -> "Bloqueado: Android no inicia el servicio de Flix"
        State.OFF -> if (isGoogleTv(context)) "Sin configurar: Inicio abre Google TV" else "Sin configurar: Inicio abre otra app"
    }

    /** Opens the screen that fixes it: the home app picker on plain Android TV, otherwise Accessibility. */
    fun fix(activity: Activity) {
        when {
            state(activity) == State.BLOCKED -> showBlockedHelp(activity)
            !isGoogleTv(activity) -> chooseDefault(activity)
            else -> openAccessibility(activity)
        }
    }

    fun openAccessibility(activity: Activity) {
        runCatching { activity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
    }

    /**
     * The system's home app setting where the TV has one; otherwise pressing Home through the
     * chooser, which asks which app to use when no default is set. If neither shows a choice
     * (another app is already the default), the help explains the one adb command.
     */
    fun chooseDefault(activity: Activity) {
        val settings = Intent(Settings.ACTION_HOME_SETTINGS)
        if (settings.resolveActivity(activity.packageManager) != null && runCatching { activity.startActivity(settings) }.isSuccess) return
        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Usar Flix como inicio")
            .setMessage("Esta TV no tiene un ajuste para la app de inicio. Pulsa Inicio: si pregunta qué app usar, elige Flix Latino y luego Siempre.\n\n" +
                "Si no pregunta, ejecuta esto una vez desde una computadora en la misma red:\n\n$DEFAULT_COMMAND\n\n" +
                "También puedes activar el servicio de Inicio de Flix en Accesibilidad.")
            .setPositiveButton("Abrir Accesibilidad") { _, _ -> openAccessibility(activity) }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    fun showBlockedHelp(activity: Activity) {
        val dialog = AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Android bloquea el servicio de Inicio")
            .setMessage("Flix Latino está activado en Accesibilidad, pero la TV no lo inicia. Android bloquea las apps instaladas desde una APK descargada.\n\n" +
                "Para permitirlo, ejecuta esto una vez desde una computadora en la misma red. Luego desactiva y activa Flix Latino en Accesibilidad:\n\n$UNBLOCK_COMMAND" +
                if (!isGoogleTv(activity)) "\n\nTambién puedes elegir Flix como app de inicio predeterminada en esta TV." else "")
            .setNegativeButton("Cerrar", null)
        if (!isGoogleTv(activity)) dialog.setPositiveButton("Usar Flix como inicio") { _, _ -> chooseDefault(activity) }
        else dialog.setPositiveButton("Abrir Accesibilidad") { _, _ -> openAccessibility(activity) }
        dialog.show()
    }
}
