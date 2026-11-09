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

    fun alternarDia(dia: Int) {
        val actuales = config.value.dias
        if (dia in actuales && actuales.size == 1) return

        val nuevos = if (dia in actuales) actuales - dia else actuales + dia
        guardar(config.value.copy(dias = nuevos))
    }

    fun cambiarOmitir(omitir: Boolean) {
        guardar(config.value.copy(omitirSiRegistrado = omitir))
    }

    private fun guardar(nueva: ConfigRecordatorio) {
        preferencias.guardarRecordatorio(nueva)
        programador.programar(nueva)
    }
}