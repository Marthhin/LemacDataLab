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

    private val _permisoPreguntado =
        MutableStateFlow(preferencias.getBoolean(CLAVE_PERMISO_PREGUNTADO, false))
    val permisoPreguntado: StateFlow<Boolean> = _permisoPreguntado.asStateFlow()

    private val _bienvenidaVista =
        MutableStateFlow(preferencias.getBoolean(CLAVE_BIENVENIDA_VISTA, false))
    val bienvenidaVista: StateFlow<Boolean> = _bienvenidaVista.asStateFlow()

    fun marcarBienvenidaVista() {
        preferencias.edit().putBoolean(CLAVE_BIENVENIDA_VISTA, true).apply()
        _bienvenidaVista.value = true
    }

    fun cambiarModoOscuro(activo: Boolean) {
        preferencias.edit().putBoolean(CLAVE_MODO_OSCURO, activo).apply()
        _modoOscuro.value = activo
    }

    fun guardarRecordatorio(config: ConfigRecordatorio) {
        preferencias.edit()
            .putBoolean(CLAVE_RECORDATORIO_ACTIVO, config.activo)
            .putInt(CLAVE_RECORDATORIO_HORA, config.hora)
            .putInt(CLAVE_RECORDATORIO_MINUTO, config.minuto)
            .putString(CLAVE_RECORDATORIO_DIAS, config.dias.sorted().joinToString(","))
            .putBoolean(CLAVE_RECORDATORIO_OMITIR, config.omitirSiRegistrado)
            .apply()
        _recordatorio.value = config
    }

    fun marcarPermisoPreguntado() {
        preferencias.edit().putBoolean(CLAVE_PERMISO_PREGUNTADO, true).apply()
        _permisoPreguntado.value = true
    }

    private fun leerModoOscuro(): Boolean? {
        if (!preferencias.contains(CLAVE_MODO_OSCURO)) return null
        return preferencias.getBoolean(CLAVE_MODO_OSCURO, false)
    }

    private fun leerRecordatorio() = ConfigRecordatorio(
        activo = preferencias.getBoolean(CLAVE_RECORDATORIO_ACTIVO, false),
        hora = preferencias.getInt(CLAVE_RECORDATORIO_HORA, HORA_POR_DEFECTO),
        minuto = preferencias.getInt(CLAVE_RECORDATORIO_MINUTO, 0),
        dias = leerDias(),
        omitirSiRegistrado = preferencias.getBoolean(CLAVE_RECORDATORIO_OMITIR, true)
    )

    private fun leerDias(): Set<Int> {
        val guardado = preferencias.getString(CLAVE_RECORDATORIO_DIAS, null)
            ?: return ConfigRecordatorio.TODOS_LOS_DIAS

        return guardado.split(",")
            .mapNotNull { it.toIntOrNull() }
            .filter { it in 1..7 }
            .toSet()
            .ifEmpty { ConfigRecordatorio.TODOS_LOS_DIAS }
    }

    private companion object {
        const val CLAVE_MODO_OSCURO = "modo_oscuro"
        const val CLAVE_RECORDATORIO_ACTIVO = "recordatorio_activo"
        const val CLAVE_RECORDATORIO_HORA = "recordatorio_hora"
        const val CLAVE_RECORDATORIO_MINUTO = "recordatorio_minuto"
        const val CLAVE_RECORDATORIO_DIAS = "recordatorio_dias"
        const val CLAVE_RECORDATORIO_OMITIR = "recordatorio_omitir"
        const val CLAVE_PERMISO_PREGUNTADO = "permiso_notificaciones_preguntado"
        const val CLAVE_BIENVENIDA_VISTA = "bienvenida_vista"
        const val HORA_POR_DEFECTO = 20
    }
}