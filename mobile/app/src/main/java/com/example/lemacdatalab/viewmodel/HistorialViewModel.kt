package com.example.lemacdatalab.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lemacdatalab.datos.BaseDatosRegistros
import com.example.lemacdatalab.model.RegistroActividad

class HistorialViewModel : ViewModel() {

    var registros by mutableStateOf<List<RegistroActividad>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf("")
        private set

    fun cargarRegistros(baseDatos: BaseDatosRegistros) {
        if (cargando) return

        cargando = true
        mensajeError = ""
        val hiloPrincipal = Handler(Looper.getMainLooper())

        Thread {
            val resultado = try {
                baseDatos.obtenerTodos()
            } catch (_: Exception) {
                null
            }

            hiloPrincipal.post {
                cargando = false

                if (resultado == null) {
                    mensajeError = "No se pudieron cargar los registros."
                } else {
                    registros = resultado
                }
            }
        }.start()
    }
}