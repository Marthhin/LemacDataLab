package com.example.lemacdatalab.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lemacdatalab.datos.PreferenciasApp

class ReceptorRecordatorio : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val config = PreferenciasApp(context).recordatorio.value
        if (!config.activo) return

        NotificacionesRecordatorio.mostrar(context)
        ProgramadorRecordatorios(context).programar(config.hora, config.minuto)
    }
}