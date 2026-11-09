package com.example.lemacdatalab.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.lemacdatalab.datos.BaseDatosRoom
import com.example.lemacdatalab.datos.PreferenciasApp
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.util.fechaDeHoy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceptorRecordatorio : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val config = PreferenciasApp(context).recordatorio.value
        if (!config.activo) return

        ProgramadorRecordatorios(context).programar(config)

        val pendiente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!config.omitirSiRegistrado || !registroHoy(context)) {
                    NotificacionesRecordatorio.mostrar(context)
                }
            } catch (error: Exception) {
                Log.e("ReceptorRecordatorio", "Error al procesar el recordatorio", error)
            } finally {
                pendiente.finish()
            }
        }
    }

    private suspend fun registroHoy(context: Context): Boolean {
        val idUsuario = SesionUsuario(context).actual.value?.idUsuario ?: return false
        val repositorio = RepositorioRegistros(
            BaseDatosRoom.obtenerInstancia(context).registroDao()
        )
        return repositorio.tieneRegistroEnFecha(idUsuario, fechaDeHoy())
    }
}