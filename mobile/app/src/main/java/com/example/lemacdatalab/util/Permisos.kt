package com.example.lemacdatalab.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

fun tienePermisoNotificaciones(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()

fun abrirAjustesNotificaciones(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    } else {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    }
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}