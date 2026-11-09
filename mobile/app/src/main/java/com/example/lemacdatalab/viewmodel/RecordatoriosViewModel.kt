package com.example.lemacdatalab.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lemacdatalab.datos.PreferenciasApp
import com.example.lemacdatalab.model.ConfigRecordatorio
import com.example.lemacdatalab.notificaciones.ProgramadorRecordatorios
import kotlinx.coroutines.flow.StateFlow

class RecordatoriosViewModel(
    private val preferencias: PreferenciasApp,
    private val programador: ProgramadorRecordatorios
) : ViewModel() {

    val config: StateFlow<ConfigRecordatorio> = preferencias.recordatorio

    fun activar() {
        guardar(config.value.copy(activo = true))
    }

    fun desactivar() {
        guardar(config.value.copy(activo = false))
    }

    fun cambiarHora(hora: Int, minuto: Int) {
        guardar(config.value.copy(hora = hora, minuto = minuto))
    }

    private fun guardar(nueva: ConfigRecordatorio) {
        preferencias.guardarRecordatorio(nueva)
        if (nueva.activo) {
            programador.programar(nueva.hora, nueva.minuto)
        } else {
            programador.cancelar()
        }
    }
}