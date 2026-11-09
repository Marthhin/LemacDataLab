package com.example.lemacdatalab.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.lemacdatalab.MainActivity
import com.example.lemacdatalab.R

object NotificacionesRecordatorio {

    const val TITULO = "Un momento para ti"
    const val TEXTO = "¿Registramos cómo va tu día?"

    private const val CANAL = "recordatorios"
    private const val ID_NOTIFICACION = 1
    private val COLOR_MARCA = 0xFF123F41.toInt()

    fun mostrar(context: Context): Boolean {
        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        val administrador = context.getSystemService(NotificationManager::class.java)

        if (!administrador.areNotificationsEnabled()) {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                "Recordatorios",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios generales de la aplicación"
            }

            administrador.createNotificationChannel(canal)

            if (administrador.getNotificationChannel(CANAL)?.importance ==
                NotificationManager.IMPORTANCE_NONE
            ) {
                return false
            }
        }

        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setColor(COLOR_MARCA)
            .setContentTitle(TITULO)
            .setContentText(TEXTO)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        return try {
            administrador.notify(ID_NOTIFICACION, notificacion)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}