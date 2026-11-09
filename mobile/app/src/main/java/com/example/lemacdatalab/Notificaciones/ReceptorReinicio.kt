package com.example.lemacdatalab.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lemacdatalab.datos.PreferenciasApp

class ReceptorReinicio : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val config = PreferenciasApp(context).recordatorio.value
        if (config.activo) {
            ProgramadorRecordatorios(context).programar(config.hora, config.minuto)
        }
    }
}