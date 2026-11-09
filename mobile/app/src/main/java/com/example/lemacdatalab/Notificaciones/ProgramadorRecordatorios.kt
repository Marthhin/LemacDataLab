package com.example.lemacdatalab.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.lemacdatalab.model.ConfigRecordatorio

class ProgramadorRecordatorios(context: Context) {

    private val contexto = context.applicationContext
    private val alarmas = contexto.getSystemService(AlarmManager::class.java)

    fun programar(config: ConfigRecordatorio) {
        val disparo = config.proximoDisparo()

        if (!config.activo || disparo == null) {
            cancelar()
            return
        }

        alarmas.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            disparo,
            intentPendiente()
        )
    }

    fun cancelar() {
        alarmas.cancel(intentPendiente())
    }

    private fun intentPendiente(): PendingIntent =
        PendingIntent.getBroadcast(
            contexto,
            CODIGO_ALARMA,
            Intent(contexto, ReceptorRecordatorio::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private companion object {
        const val CODIGO_ALARMA = 100
    }
}