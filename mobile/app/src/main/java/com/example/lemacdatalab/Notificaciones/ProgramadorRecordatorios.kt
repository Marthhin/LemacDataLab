package com.example.lemacdatalab.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class ProgramadorRecordatorios(context: Context) {

    private val contexto = context.applicationContext
    private val alarmas = contexto.getSystemService(AlarmManager::class.java)

    fun programar(hora: Int, minuto: Int) {
        alarmas.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            proximoDisparo(hora, minuto),
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

    private fun proximoDisparo(hora: Int, minuto: Int): Long {
        val disparo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (disparo.timeInMillis <= System.currentTimeMillis()) {
            disparo.add(Calendar.DAY_OF_YEAR, 1)
        }
        return disparo.timeInMillis
    }

    private companion object {
        const val CODIGO_ALARMA = 100
    }
}