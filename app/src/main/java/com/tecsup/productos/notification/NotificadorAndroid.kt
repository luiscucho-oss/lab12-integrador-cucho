package com.tecsup.productos.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.productos.MainActivity
import com.tecsup.productos.R

/** Implementación real del Notificador. Es la única clase que necesita Context. */
class NotificadorAndroid(context: Context) : Notificador {

    private val context = context.applicationContext

    init {
        crearCanal()
    }

    private fun crearCanal() {
        val canal = NotificationChannel(
            CANAL_ID,
            "Sincronización",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos al terminar de descargar el catálogo"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(canal)
    }

    override fun mostrar(titulo: String, texto: String) {
        // Si la persona negó el permiso, la app sigue normal y simplemente no avisa
        if (!tienePermiso()) return

        // Al tocar la notificación se abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICACION_ID, notificacion)
        } catch (e: SecurityException) {
            // El permiso se retiró justo en este momento: no se muestra nada
        }
    }

    private fun tienePermiso(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val CANAL_ID = "sincronizacion"
        const val NOTIFICACION_ID = 1
    }
}
