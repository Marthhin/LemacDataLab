package com.example.lemacdatalab.datos

import android.content.Context
import com.example.lemacdatalab.model.ConfigRecordatorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenciasApp(context: Context) {

    private val preferencias =
        context.getSharedPreferences("preferencias_lemac", Context.MODE_PRIVATE)

    private val _modoOscuro = MutableStateFlow(leerModoOscuro())

    val modoOscuro: StateFlow<Boolean?> = _modoOscuro.asStateFlow()

    private val _recordatorio = MutableStateFlow(leerRecordatorio())

    val recordatorio: StateFlow<ConfigRecordatorio> = _recordatorio.asStateFlow()

    fun cambiarModoOscuro(activo: Boolean) {
        preferencias.edit().putBoolean(CLAVE_MODO_OSCURO, activo).apply()
        _modoOscuro.value = activo
    }

    fun guardarRecordatorio(config: ConfigRecordatorio) {
        preferencias.edit()
            .putBoolean(CLAVE_RECORDATORIO_ACTIVO, config.activo)
            .putInt(CLAVE_RECORDATORIO_HORA, config.hora)
            .putInt(CLAVE_RECORDATORIO_MINUTO, config.minuto)
            .apply()
        _recordatorio.value = config
    }

    private fun leerModoOscuro(): Boolean? {
        if (!preferencias.contains(CLAVE_MODO_OSCURO)) return null
        return preferencias.getBoolean(CLAVE_MODO_OSCURO, false)
    }

    private fun leerRecordatorio() = ConfigRecordatorio(
        activo = preferencias.getBoolean(CLAVE_RECORDATORIO_ACTIVO, false),
        hora = preferencias.getInt(CLAVE_RECORDATORIO_HORA, HORA_POR_DEFECTO),
        minuto = preferencias.getInt(CLAVE_RECORDATORIO_MINUTO, 0)
    )

    private companion object {
        const val CLAVE_MODO_OSCURO = "modo_oscuro"
        const val CLAVE_RECORDATORIO_ACTIVO = "recordatorio_activo"
        const val CLAVE_RECORDATORIO_HORA = "recordatorio_hora"
        const val CLAVE_RECORDATORIO_MINUTO = "recordatorio_minuto"
        const val HORA_POR_DEFECTO = 20
    }
}